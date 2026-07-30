package com.moneymoment.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val description: String,
    val category: String,
    val date: Long,
    val verdict: String? = null,
    val verdictScore: Int? = null,
    val rated: Boolean = false,
    val regretted: Boolean? = null,
    val ratedAt: Long? = null
)
