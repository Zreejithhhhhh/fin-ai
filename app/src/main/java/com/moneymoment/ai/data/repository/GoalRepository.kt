package com.moneymoment.ai.data.repository

import com.moneymoment.ai.data.local.dao.GoalDao
import com.moneymoment.ai.data.local.entity.GoalEntity
import com.moneymoment.ai.domain.model.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoalRepository(private val goalDao: GoalDao) {

    fun getAllGoals(): Flow<List<Goal>> {
        return goalDao.getAllGoals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getActiveGoals(): Flow<List<Goal>> {
        return goalDao.getActiveGoals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addGoal(
        name: String,
        targetAmount: Double,
        icon: String = "\uD83C\uDFAF"
    ): Long {
        val entity = GoalEntity(
            name = name,
            targetAmount = targetAmount,
            icon = icon,
            createdAt = System.currentTimeMillis()
        )
        return goalDao.insert(entity)
    }

    suspend fun updateGoalSavings(id: Long, amount: Double) {
        val entity = goalDao.getGoalById(id) ?: return
        val updated = entity.copy(savedAmount = amount)
        goalDao.update(updated)
    }

    suspend fun toggleGoalActive(id: Long) {
        val entity = goalDao.getGoalById(id) ?: return
        val updated = entity.copy(isActive = !entity.isActive)
        goalDao.update(updated)
    }

    suspend fun deleteGoal(id: Long) {
        val entity = goalDao.getGoalById(id) ?: return
        goalDao.delete(entity)
    }
}

private fun GoalEntity.toDomain(): Goal = Goal(
    id = id,
    name = name,
    targetAmount = targetAmount,
    savedAmount = savedAmount,
    icon = icon,
    isActive = isActive,
    createdAt = createdAt
)

private fun Goal.toEntity(): GoalEntity = GoalEntity(
    id = id,
    name = name,
    targetAmount = targetAmount,
    savedAmount = savedAmount,
    icon = icon,
    isActive = isActive,
    createdAt = createdAt
)
