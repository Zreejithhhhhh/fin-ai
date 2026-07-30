package com.moneymoment.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.moneymoment.ai.data.local.entity.PurchaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Insert
    suspend fun insert(purchase: PurchaseEntity): Long

    @Update
    suspend fun update(purchase: PurchaseEntity)

    @Delete
    suspend fun delete(purchase: PurchaseEntity)

    @Query("SELECT * FROM purchases ORDER BY date DESC")
    fun getAllPurchases(): Flow<List<PurchaseEntity>>

    @Query("SELECT * FROM purchases WHERE rated = 0 ORDER BY date DESC")
    fun getUnratedPurchases(): Flow<List<PurchaseEntity>>

    @Query("SELECT * FROM purchases WHERE date >= :since ORDER BY date DESC")
    fun getPurchasesSince(since: Long): Flow<List<PurchaseEntity>>

    @Query("SELECT * FROM purchases WHERE id = :id")
    suspend fun getPurchaseById(id: Long): PurchaseEntity?

    @Query("SELECT COUNT(*) FROM purchases WHERE category = :category AND regretted = 1 AND rated = 1")
    fun getRegrettedCount(category: String): Flow<Int>
}
