package com.moneymoment.ai.domain.model

data class Purchase(
    val id: Long = 0,
    val amount: Double,
    val description: String,
    val category: String,
    val date: Long = System.currentTimeMillis(),
    val verdict: String? = null,
    val verdictScore: Int? = null,
    val rated: Boolean = false,
    val regretted: Boolean? = null,
    val ratedAt: Long? = null
)
