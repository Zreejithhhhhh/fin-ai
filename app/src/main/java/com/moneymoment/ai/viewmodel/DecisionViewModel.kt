package com.moneymoment.ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneymoment.ai.data.local.AppDatabase
import com.moneymoment.ai.data.repository.GoalRepository
import com.moneymoment.ai.data.repository.PurchaseRepository
import com.moneymoment.ai.domain.engine.AIEngine
import com.moneymoment.ai.domain.model.Goal
import com.moneymoment.ai.domain.model.Purchase
import com.moneymoment.ai.domain.model.Verdict
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DecisionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val purchaseRepository = PurchaseRepository(db.purchaseDao())
    private val goalRepository = GoalRepository(db.goalDao())

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _verdict = MutableStateFlow<Verdict?>(null)
    val verdict: StateFlow<Verdict?> = _verdict.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var purchases: List<Purchase> = emptyList()
    private var goals: List<Goal> = emptyList()

    init {
        viewModelScope.launch {
            purchaseRepository.getAllPurchases().collectLatest { list ->
                purchases = list
            }
        }
        viewModelScope.launch {
            goalRepository.getAllGoals().collectLatest { list ->
                goals = list
            }
        }
    }

    fun setAmount(value: String) {
        _amount.value = value
    }

    fun setDescription(value: String) {
        _description.value = value
    }

    fun setCategory(value: String) {
        _selectedCategory.value = value
    }

    fun checkPurchase() {
        viewModelScope.launch {
            val amountVal = _amount.value.toDoubleOrNull() ?: return@launch
            if (_selectedCategory.value.isBlank() || _description.value.isBlank()) return@launch
            _isLoading.value = true
            _verdict.value = AIEngine.evaluatePurchase(
                amount = amountVal,
                category = _selectedCategory.value,
                description = _description.value,
                purchases = purchases,
                goals = goals
            )
            _isLoading.value = false
        }
    }

    fun logPurchase() {
        viewModelScope.launch {
            val amountVal = _amount.value.toDoubleOrNull() ?: return@launch
            val v = _verdict.value ?: return@launch
            purchaseRepository.addPurchase(
                amount = amountVal,
                description = _description.value,
                category = _selectedCategory.value,
                verdict = v.verdict,
                verdictScore = v.score
            )
            _amount.value = ""
            _description.value = ""
            _selectedCategory.value = ""
            _verdict.value = null
        }
    }
}
