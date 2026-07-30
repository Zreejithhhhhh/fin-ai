package com.moneymoment.ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneymoment.ai.data.local.AppDatabase
import com.moneymoment.ai.data.repository.GoalRepository
import com.moneymoment.ai.domain.model.Goal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GoalsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val goalRepository = GoalRepository(db.goalDao())

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()

    private val _showAddForm = MutableStateFlow(false)
    val showAddForm: StateFlow<Boolean> = _showAddForm.asStateFlow()

    init {
        viewModelScope.launch {
            goalRepository.getAllGoals().collectLatest { list ->
                _goals.value = list
            }
        }
    }

    fun addGoal(name: String, target: Double, icon: String = "dart") {
        viewModelScope.launch {
            val activeCount = _goals.value.count { it.isActive }
            if (activeCount >= 3) return@launch
            goalRepository.addGoal(name, target, icon)
            _showAddForm.value = false
        }
    }

    fun addSavings(id: Long, amount: Double) {
        viewModelScope.launch {
            val goal = _goals.value.find { it.id == id } ?: return@launch
            goalRepository.updateGoalSavings(id, goal.savedAmount + amount)
        }
    }

    fun toggleActive(id: Long) {
        viewModelScope.launch {
            val goal = _goals.value.find { it.id == id } ?: return@launch
            if (goal.isActive) {
                goalRepository.toggleGoalActive(id)
            } else {
                val activeCount = _goals.value.count { it.isActive }
                if (activeCount >= 3) return@launch
                goalRepository.toggleGoalActive(id)
            }
        }
    }

    fun deleteGoal(id: Long) {
        viewModelScope.launch {
            goalRepository.deleteGoal(id)
        }
    }

    fun toggleForm() {
        _showAddForm.value = !_showAddForm.value
    }
}
