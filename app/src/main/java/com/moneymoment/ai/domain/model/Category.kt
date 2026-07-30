package com.moneymoment.ai.domain.model

object Category {

    val ALL_CATEGORIES = listOf(
        "Food & Dining",
        "Groceries",
        "Transport",
        "Shopping",
        "Entertainment",
        "Bills & Utilities",
        "Health",
        "Education",
        "Travel",
        "Clothing",
        "Electronics",
        "Subscriptions",
        "Gifts",
        "Other"
    )

    val DEFAULT_REGRET_RATES: Map<String, Double> = mapOf(
        "Food & Dining" to 0.25,
        "Groceries" to 0.10,
        "Transport" to 0.15,
        "Shopping" to 0.50,
        "Entertainment" to 0.55,
        "Bills & Utilities" to 0.05,
        "Health" to 0.20,
        "Education" to 0.20,
        "Travel" to 0.45,
        "Clothing" to 0.55,
        "Electronics" to 0.60,
        "Subscriptions" to 0.40,
        "Gifts" to 0.25,
        "Other" to 0.35
    )

    fun getCategoryIcon(category: String): String {
        return when (category) {
            "Food & Dining" -> "restaurant"
            "Groceries" -> "local_grocery_store"
            "Transport" -> "directions_car"
            "Shopping" -> "shopping_bag"
            "Entertainment" -> "celebration"
            "Bills & Utilities" -> "receipt_long"
            "Health" -> "local_hospital"
            "Education" -> "school"
            "Travel" -> "flight"
            "Clothing" -> "checkroom"
            "Electronics" -> "devices"
            "Subscriptions" -> "subscriptions"
            "Gifts" -> "card_giftcard"
            else -> "category"
        }
    }
}
