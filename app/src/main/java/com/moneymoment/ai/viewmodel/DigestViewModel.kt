package com.moneymoment.ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneymoment.ai.data.local.AppDatabase
import com.moneymoment.ai.data.local.entity.SettingsEntity
import com.moneymoment.ai.data.repository.PurchaseRepository
import com.moneymoment.ai.domain.engine.AIEngine
import com.moneymoment.ai.domain.engine.BestCategoryInfo
import com.moneymoment.ai.domain.engine.WeeklyDigest
import com.moneymoment.ai.domain.engine.WorstCategoryInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject

class DigestViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val purchaseRepository = PurchaseRepository(db.purchaseDao())
    private val settingsDao = db.settingsDao()

    private val _currentDigest = MutableStateFlow<WeeklyDigest?>(null)
    val currentDigest: StateFlow<WeeklyDigest?> = _currentDigest.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadLastDigest()
    }

    private fun loadLastDigest() {
        viewModelScope.launch {
            val json = settingsDao.getValue("last_digest")
            if (json != null) {
                _currentDigest.value = parseDigest(json)
            }
        }
    }

    fun generateDigest() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val purchases = purchaseRepository.getAllPurchases().first()
                val digest = AIEngine.generateWeeklyDigest(purchases)
                _currentDigest.value = digest
                saveDigest(digest)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to generate digest"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun saveDigest(digest: WeeklyDigest) {
        val json = digestToJson(digest)
        settingsDao.set(SettingsEntity(key = "last_digest", value = json))
    }

    private fun digestToJson(digest: WeeklyDigest): String {
        val obj = JSONObject()
        obj.put("hasData", digest.hasData)
        obj.put("weekStart", digest.weekStart)
        obj.put("overallRate", digest.overallRate)
        obj.put("totalPurchases", digest.totalPurchases)
        obj.put("totalSpent", digest.totalSpent)
        obj.put("totalRegrettedSpent", digest.totalRegrettedSpent)
        obj.put("tip", digest.tip)
        digest.worstCategory?.let { wc ->
            val wcObj = JSONObject()
            wcObj.put("name", wc.name)
            wcObj.put("rate", wc.rate)
            wcObj.put("count", wc.count)
            wcObj.put("regretted", wc.regretted)
            wcObj.put("wasted", wc.wasted)
            obj.put("worstCategory", wcObj)
        }
        digest.bestCategory?.let { bc ->
            val bcObj = JSONObject()
            bcObj.put("name", bc.name)
            bcObj.put("rate", bc.rate)
            obj.put("bestCategory", bcObj)
        }
        return obj.toString()
    }

    private fun parseDigest(json: String): WeeklyDigest? {
        return try {
            val obj = JSONObject(json)
            val worstCategory = if (obj.has("worstCategory") && !obj.isNull("worstCategory")) {
                val wc = obj.getJSONObject("worstCategory")
                WorstCategoryInfo(
                    name = wc.getString("name"),
                    rate = wc.getInt("rate"),
                    count = wc.getInt("count"),
                    regretted = wc.getInt("regretted"),
                    wasted = wc.getDouble("wasted")
                )
            } else null
            val bestCategory = if (obj.has("bestCategory") && !obj.isNull("bestCategory")) {
                val bc = obj.getJSONObject("bestCategory")
                BestCategoryInfo(
                    name = bc.getString("name"),
                    rate = bc.getInt("rate")
                )
            } else null
            WeeklyDigest(
                hasData = obj.getBoolean("hasData"),
                weekStart = obj.getLong("weekStart"),
                overallRate = obj.getInt("overallRate"),
                totalPurchases = obj.getInt("totalPurchases"),
                totalSpent = obj.getDouble("totalSpent"),
                totalRegrettedSpent = obj.getDouble("totalRegrettedSpent"),
                tip = obj.getString("tip"),
                worstCategory = worstCategory,
                bestCategory = bestCategory
            )
        } catch (e: Exception) {
            null
        }
    }
}
