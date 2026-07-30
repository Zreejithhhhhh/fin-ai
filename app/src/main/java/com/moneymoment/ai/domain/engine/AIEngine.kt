package com.moneymoment.ai.domain.engine

import com.moneymoment.ai.domain.model.Category
import com.moneymoment.ai.domain.model.Goal
import com.moneymoment.ai.domain.model.GoalImpact
import com.moneymoment.ai.domain.model.Purchase
import com.moneymoment.ai.domain.model.Verdict
import kotlin.math.min
import kotlin.math.roundToInt

data class WeeklyDigest(
    val hasData: Boolean,
    val weekStart: Long,
    val overallRate: Int,
    val totalPurchases: Int,
    val totalSpent: Double,
    val totalRegrettedSpent: Double,
    val worstCategory: WorstCategoryInfo? = null,
    val bestCategory: BestCategoryInfo? = null,
    val tip: String
)

data class WorstCategoryInfo(
    val name: String,
    val rate: Int,
    val count: Int,
    val regretted: Int,
    val wasted: Double
)

data class BestCategoryInfo(
    val name: String,
    val rate: Int
)

data class MonthlyStats(
    val totalPurchases: Int,
    val totalSpent: Double,
    val ratedCount: Int,
    val regrettedCount: Int,
    val regretRate: Int,
    val savingsRate: Int,
    val totalIncome: Double
)

data class CategoryStats(
    val count: Int,
    val total: Double,
    val regretted: Int
)

object AIEngine {

    private const val ONE_WEEK_MS = 7L * 24 * 60 * 60 * 1000

    suspend fun evaluatePurchase(
        amount: Double,
        category: String,
        description: String,
        purchases: List<Purchase>,
        goals: List<Goal>
    ): Verdict {
        val now = System.currentTimeMillis()
        val oneMonthAgo = now - 30L * 24 * 60 * 60 * 1000

        val recentPurchases = purchases.filter { it.date >= oneMonthAgo }
        val categoryPurchases = recentPurchases.filter { it.category == category }

        val categoryRegretRate = calculateCategoryRegretRate(category, purchases)

        val recencyWeight = calculateRecencyWeight(category, purchases)

        val amountRatio = if (categoryPurchases.isNotEmpty()) {
            val avgAmount = categoryPurchases.map { it.amount }.average()
            if (avgAmount > 0) min(amount / avgAmount, 3.0) / 3.0 else 0.3
        } else {
            0.3
        }

        val regretScore = categoryRegretRate * 0.4 + recencyWeight * 0.3 + amountRatio * 0.3

        val scoreNormalized = min(regretScore, 1.0)
        val score = (scoreNormalized * 100).roundToInt()

        val verdict = when {
            score < 35 -> "green"
            score < 70 -> "yellow"
            else -> "red"
        }

        val goalImpact = findGoalImpact(amount, goals)

        val monthlyCount = categoryPurchases.size

        val reason = buildReason(verdict, amount, category, description, score, categoryRegretRate, goalImpact)

        return Verdict(
            verdict = verdict,
            score = score,
            reason = reason,
            goalImpact = goalImpact,
            categoryRegretRate = (categoryRegretRate * 100).roundToInt(),
            monthlyCount = monthlyCount
        )
    }

    suspend fun generateWeeklyDigest(purchases: List<Purchase>): WeeklyDigest {
        val now = System.currentTimeMillis()
        val weekStart = now - (now % ONE_WEEK_MS)

        val weekPurchases = purchases.filter {
            it.date >= weekStart && it.date < weekStart + ONE_WEEK_MS
        }

        if (weekPurchases.isEmpty()) {
            return WeeklyDigest(
                hasData = false,
                weekStart = weekStart,
                overallRate = 0,
                totalPurchases = 0,
                totalSpent = 0.0,
                totalRegrettedSpent = 0.0,
                tip = "No purchases recorded this week yet."
            )
        }

        val totalSpent = weekPurchases.sumOf { it.amount }
        val rated = weekPurchases.filter { it.rated }
        val regretted = weekPurchases.filter { it.regretted == true }
        val regrettedSpent = regretted.sumOf { it.amount }

        val categoryStats = weekPurchases.groupBy { it.category }.mapValues { (_, list) ->
            val regrettedCount = list.count { it.regretted == true }
            CategoryStats(
                count = list.size,
                total = list.sumOf { it.amount },
                regretted = regrettedCount
            )
        }

        val worstCategory = categoryStats
            .filter { it.value.count >= 2 }
            .maxByOrNull { (_, stats) ->
                if (stats.count > 0) stats.regretted.toDouble() / stats.count else 0.0
            }
            ?.let { (name, stats) ->
                val wasted = weekPurchases
                    .filter { it.category == name && it.regretted == true }
                    .sumOf { it.amount }
                WorstCategoryInfo(
                    name = name,
                    rate = if (stats.count > 0) (stats.regretted * 100 / stats.count) else 0,
                    count = stats.count,
                    regretted = stats.regretted,
                    wasted = wasted
                )
            }

        val bestCategory = categoryStats
            .filter { it.value.count >= 2 }
            .minByOrNull { (_, stats) ->
                if (stats.count > 0) stats.regretted.toDouble() / stats.count else 0.0
            }
            ?.let { (name, stats) ->
                BestCategoryInfo(
                    name = name,
                    rate = if (stats.count > 0) (stats.regretted * 100 / stats.count) else 0
                )
            }

        val overallRate = if (rated.isNotEmpty()) {
            (regretted.size * 100 / rated.size)
        } else 0

        val tip = generateWeeklyTip(overallRate, worstCategory, totalSpent, regrettedSpent)

        return WeeklyDigest(
            hasData = true,
            weekStart = weekStart,
            overallRate = overallRate,
            totalPurchases = weekPurchases.size,
            totalSpent = totalSpent,
            totalRegrettedSpent = regrettedSpent,
            worstCategory = worstCategory,
            bestCategory = bestCategory,
            tip = tip
        )
    }

    suspend fun getMonthlyStats(purchases: List<Purchase>, monthlyIncome: Double): MonthlyStats {
        val now = System.currentTimeMillis()
        val oneMonthAgo = now - 30L * 24 * 60 * 60 * 1000

        val monthPurchases = purchases.filter { it.date >= oneMonthAgo }
        val totalSpent = monthPurchases.sumOf { it.amount }
        val rated = monthPurchases.filter { it.rated }
        val regretted = monthPurchases.filter { it.regretted == true }

        val regretRate = if (rated.isNotEmpty()) {
            (regretted.size * 100 / rated.size)
        } else 0

        val savingsRate = if (monthlyIncome > 0) {
            ((monthlyIncome - totalSpent) / monthlyIncome * 100).roundToInt()
                .coerceIn(0, 100)
        } else 0

        return MonthlyStats(
            totalPurchases = monthPurchases.size,
            totalSpent = totalSpent,
            ratedCount = rated.size,
            regrettedCount = regretted.size,
            regretRate = regretRate,
            savingsRate = savingsRate,
            totalIncome = monthlyIncome
        )
    }

    suspend fun getCategoryBreakdown(purchases: List<Purchase>): Map<String, CategoryStats> {
        val now = System.currentTimeMillis()
        val oneMonthAgo = now - 30L * 24 * 60 * 60 * 1000

        val recent = purchases.filter { it.date >= oneMonthAgo }

        return recent.groupBy { it.category }.mapValues { (_, list) ->
            CategoryStats(
                count = list.size,
                total = list.sumOf { it.amount },
                regretted = list.count { it.regretted == true }
            )
        }
    }

    private fun calculateCategoryRegretRate(category: String, purchases: List<Purchase>): Double {
        val categoryPurchases = purchases.filter { it.category == category }
        if (categoryPurchases.isEmpty()) {
            return Category.DEFAULT_REGRET_RATES[category] ?: 0.35
        }

        val rated = categoryPurchases.filter { it.rated }
        if (rated.isEmpty()) {
            return Category.DEFAULT_REGRET_RATES[category] ?: 0.35
        }

        val regrettedCount = rated.count { it.regretted == true }
        return regrettedCount.toDouble() / rated.size
    }

    private fun calculateRecencyWeight(category: String, purchases: List<Purchase>): Double {
        val now = System.currentTimeMillis()
        val oneMonthAgo = now - 30L * 24 * 60 * 60 * 1000

        val regrettable = purchases.filter {
            it.category == category && it.regretted == true && it.date >= oneMonthAgo
        }

        if (regrettable.isEmpty()) return 0.0

        val recencyScore = regrettable.maxOf { purchase ->
            val daysAgo = (now - purchase.date) / (24 * 60 * 60 * 1000.0)
            (30.0 - daysAgo) / 30.0
        }

        return recencyScore.coerceIn(0.0, 1.0)
    }

    private fun findGoalImpact(amount: Double, goals: List<Goal>): GoalImpact? {
        val activeGoals = goals.filter { it.isActive }
        if (activeGoals.isEmpty()) return null

        val goal = activeGoals.minByOrNull { it.savedAmount / it.targetAmount } ?: return null

        val remaining = goal.targetAmount - goal.savedAmount
        if (remaining <= 0) return null

        val percentImpact = String.format("%.1f", (amount / goal.targetAmount) * 100)
        val daysImpact = if (remaining > 0) {
            ((amount / remaining) * 30).roundToInt().coerceAtLeast(1)
        } else 1

        return GoalImpact(
            goalName = goal.name,
            goalTarget = goal.targetAmount,
            goalSaved = goal.savedAmount,
            remaining = remaining,
            daysImpact = daysImpact,
            percentImpact = percentImpact
        )
    }

    private fun buildReason(
        verdict: String,
        amount: Double,
        category: String,
        description: String,
        score: Int,
        categoryRegretRate: Double,
        goalImpact: GoalImpact?
    ): String {
        val parts = mutableListOf<String>()

        when (verdict) {
            "green" -> parts.add("This looks like a sensible purchase.")
            "yellow" -> parts.add("Consider whether this purchase is necessary.")
            "red" -> parts.add("This purchase may be one you will regret.")
        }

        val regretPercent = (categoryRegretRate * 100).roundToInt()
        if (regretPercent > 30) {
            parts.add("Purchases in $category are regretted $regretPercent% of the time.")
        } else if (regretPercent > 0) {
            parts.add("You rarely regret purchases in $category.")
        }

        if (goalImpact != null) {
            parts.add(
                "This is ${goalImpact.percentImpact}% of your '${goalImpact.goalName}' goal " +
                        "and could delay it by ${goalImpact.daysImpact} days."
            )
        }

        return parts.joinToString(" ")
    }

    private fun generateWeeklyTip(
        overallRate: Int,
        worstCategory: WorstCategoryInfo?,
        totalSpent: Double,
        regrettedSpent: Double
    ): String {
        if (overallRate >= 70) {
            return "Most of your rated purchases this week were regretted. Try pausing " +
                    "before non-essential purchases and ask yourself if you really need them."
        }
        if (worstCategory != null && worstCategory.rate >= 50) {
            return "Your highest regret category is '${worstCategory.name}' with " +
                    "${worstCategory.regretted} regretted purchases. Consider setting a " +
                    "monthly limit for this category."
        }
        if (regrettedSpent > 0) {
            val wastedPercent = ((regrettedSpent / totalSpent) * 100).roundToInt()
            return "You spent $wastedPercent% of your budget on purchases you later regretted. " +
                    "Try using the 24-hour rule before buying non-essentials."
        }
        return "Great week! Keep up the mindful spending habits."
    }
}
