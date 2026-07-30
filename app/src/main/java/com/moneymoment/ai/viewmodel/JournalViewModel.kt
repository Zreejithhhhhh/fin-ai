package com.moneymoment.ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneymoment.ai.data.local.AppDatabase
import com.moneymoment.ai.data.repository.PurchaseRepository
import com.moneymoment.ai.domain.model.Purchase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class JournalStats(
    val totalRated: Int = 0,
    val totalRegretted: Int = 0,
    val regretRate: Int = 0,
    val categoryBreakdown: Map<String, Pair<Int, Int>> = emptyMap()
)

class JournalViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val purchaseRepository = PurchaseRepository(db.purchaseDao())

    private val _unratedPurchases = MutableStateFlow<List<Purchase>>(emptyList())
    val unratedPurchases: StateFlow<List<Purchase>> = _unratedPurchases.asStateFlow()

    private val _allPurchases = MutableStateFlow<List<Purchase>>(emptyList())
    val allPurchases: StateFlow<List<Purchase>> = _allPurchases.asStateFlow()

    private val _stats = MutableStateFlow(JournalStats())
    val stats: StateFlow<JournalStats> = _stats.asStateFlow()

    init {
        viewModelScope.launch {
            purchaseRepository.getUnratedPurchases().collectLatest { list ->
                _unratedPurchases.value = list
            }
        }
        viewModelScope.launch {
            purchaseRepository.getAllPurchases().collectLatest { list ->
                _allPurchases.value = list
                computeStats(list)
            }
        }
    }

    fun ratePurchase(id: Long, regretted: Boolean) {
        viewModelScope.launch {
            purchaseRepository.ratePurchase(id, regretted)
        }
    }

    fun refresh() {
        computeStats(_allPurchases.value)
    }

    private fun computeStats(purchases: List<Purchase>) {
        val rated = purchases.filter { it.rated }
        val regretted = purchases.filter { it.regretted == true }
        val totalRated = rated.size
        val totalRegretted = regretted.size
        val regretRate = if (totalRated > 0) (totalRegretted * 100 / totalRated) else 0
        val categoryBreakdown = purchases
            .filter { it.rated }
            .groupBy { it.category }
            .mapValues { (_, list) ->
                val regrettedCount = list.count { it.regretted == true }
                list.size to regrettedCount
            }
        _stats.value = JournalStats(
            totalRated = totalRated,
            totalRegretted = totalRegretted,
            regretRate = regretRate,
            categoryBreakdown = categoryBreakdown
        )
    }
}
