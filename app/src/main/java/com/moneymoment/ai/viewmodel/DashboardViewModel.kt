package com.moneymoment.ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneymoment.ai.data.local.AppDatabase
import com.moneymoment.ai.data.local.entity.SettingsEntity
import com.moneymoment.ai.data.repository.GoalRepository
import com.moneymoment.ai.data.repository.PurchaseRepository
import com.moneymoment.ai.domain.engine.AIEngine
import com.moneymoment.ai.domain.model.CategoryStats
import com.moneymoment.ai.domain.model.Goal
import com.moneymoment.ai.domain.model.MonthlyStats
import com.moneymoment.ai.domain.model.Purchase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val purchaseRepository = PurchaseRepository(db.purchaseDao())
    private val goalRepository = GoalRepository(db.goalDao())
    private val settingsDao = db.settingsDao()

    private val _monthlyIncome = MutableStateFlow("")
    val monthlyIncome: StateFlow<String> = _monthlyIncome.asStateFlow()

    private val _monthlyStats = MutableStateFlow<MonthlyStats?>(null)
    val monthlyStats: StateFlow<MonthlyStats?> = _monthlyStats.asStateFlow()

    private val _categoryBreakdown = MutableStateFlow<Map<String, CategoryStats>>(emptyMap())
    val categoryBreakdown: StateFlow<Map<String, CategoryStats>> = _categoryBreakdown.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var purchases: List<Purchase> = emptyList()
    @Suppress("unused")
    private var goals: List<Goal> = emptyList()

    init {
        loadMonthlyIncome()
        viewModelScope.launch {
            purchaseRepository.getAllPurchases().collectLatest { list ->
                purchases = list
                refreshStats()
            }
        }
        viewModelScope.launch {
            goalRepository.getAllGoals().collectLatest { list ->
                goals = list
            }
        }
    }

    fun loadMonthlyIncome() {
        viewModelScope.launch {
            val income = settingsDao.getValue("monthly_income")
            _monthlyIncome.value = income ?: ""
        }
    }

    fun setIncome(value: String) {
        _monthlyIncome.value = value
        viewModelScope.launch {
            settingsDao.set(SettingsEntity(key = "monthly_income", value = value))
            refreshStats()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            loadMonthlyIncome()
            refreshStats()
        }
    }

    private suspend fun refreshStats() {
        _isLoading.value = true
        val income = _monthlyIncome.value.toDoubleOrNull() ?: 0.0
        _monthlyStats.value = AIEngine.getMonthlyStats(purchases, income)
        _categoryBreakdown.value = AIEngine.getCategoryBreakdown(purchases)
        _isLoading.value = false
    }
}
