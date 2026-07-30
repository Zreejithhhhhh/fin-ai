package com.moneymoment.ai.domain.model

data class Verdict(
    val verdict: String,
    val score: Int,
    val reason: String,
    val goalImpact: GoalImpact? = null,
    val categoryRegretRate: Int = 0,
    val monthlyCount: Int = 0
)

data class GoalImpact(
    val goalName: String,
    val goalTarget: Double,
    val goalSaved: Double,
    val remaining: Double,
    val daysImpact: Int,
    val percentImpact: String
)
