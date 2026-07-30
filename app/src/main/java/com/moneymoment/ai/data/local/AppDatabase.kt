package com.moneymoment.ai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.moneymoment.ai.data.local.dao.GoalDao
import com.moneymoment.ai.data.local.dao.PurchaseDao
import com.moneymoment.ai.data.local.dao.SettingsDao
import com.moneymoment.ai.data.local.entity.GoalEntity
import com.moneymoment.ai.data.local.entity.PurchaseEntity
import com.moneymoment.ai.data.local.entity.SettingsEntity

@Database(
    entities = [PurchaseEntity::class, GoalEntity::class, SettingsEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun purchaseDao(): PurchaseDao
    abstract fun goalDao(): GoalDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "moneymoment.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
