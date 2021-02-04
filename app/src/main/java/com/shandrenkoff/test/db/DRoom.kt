package com.shandrenkoff.test.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shandrenkoff.test.dao.CurrencyDao
import com.shandrenkoff.test.entities.CurrencyEntity

@Database(
    entities = [CurrencyEntity::class],
    version = 1
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}