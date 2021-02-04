package com.shandrenkoff.test.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyEntity(
    @PrimaryKey
    val name: String,
    val value: Double?
)