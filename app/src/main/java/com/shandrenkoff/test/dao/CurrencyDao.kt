package com.shandrenkoff.test.dao

import androidx.room.*
import com.shandrenkoff.test.entities.CurrencyEntity

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currencyentity")
    fun getAll(): List<CurrencyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    fun insertAll(vararg entities: CurrencyEntity)

    @Delete
    fun delete(entities: Array<out CurrencyEntity>)
}
