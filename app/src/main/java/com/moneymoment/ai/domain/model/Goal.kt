package com.moneymoment.ai.domain.model

data class Goal(
    val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val icon: String = "dart",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
