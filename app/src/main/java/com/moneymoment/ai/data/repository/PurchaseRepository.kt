package com.moneymoment.ai.data.repository

import com.moneymoment.ai.data.local.dao.PurchaseDao
import com.moneymoment.ai.data.local.entity.PurchaseEntity
import com.moneymoment.ai.domain.model.Purchase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PurchaseRepository(private val purchaseDao: PurchaseDao) {

    fun getAllPurchases(): Flow<List<Purchase>> {
        return purchaseDao.getAllPurchases().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getUnratedPurchases(): Flow<List<Purchase>> {
        return purchaseDao.getUnratedPurchases().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getPurchasesSince(since: Long): Flow<List<Purchase>> {
        return purchaseDao.getPurchasesSince(since).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addPurchase(
        amount: Double,
        description: String,
        category: String,
        verdict: String? = null,
        verdictScore: Int? = null
    ): Long {
        val entity = PurchaseEntity(
            amount = amount,
            description = description,
            category = category,
            date = System.currentTimeMillis(),
            verdict = verdict,
            verdictScore = verdictScore
        )
        return purchaseDao.insert(entity)
    }

    suspend fun ratePurchase(id: Long, regretted: Boolean) {
        val entity = purchaseDao.getPurchaseById(id) ?: return
        val updated = entity.copy(
            rated = true,
            regretted = regretted,
            ratedAt = System.currentTimeMillis()
        )
        purchaseDao.update(updated)
    }

    suspend fun deletePurchase(id: Long) {
        val entity = purchaseDao.getPurchaseById(id) ?: return
        purchaseDao.delete(entity)
    }
}

private fun PurchaseEntity.toDomain(): Purchase = Purchase(
    id = id,
    amount = amount,
    description = description,
    category = category,
    date = date,
    verdict = verdict,
    verdictScore = verdictScore,
    rated = rated,
    regretted = regretted,
    ratedAt = ratedAt
)

private fun Purchase.toEntity(): PurchaseEntity = PurchaseEntity(
    id = id,
    amount = amount,
    description = description,
    category = category,
    date = date,
    verdict = verdict,
    verdictScore = verdictScore,
    rated = rated,
    regretted = regretted,
    ratedAt = ratedAt
)
