package io.farzadmoradkani.auval.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gold_entries")
data class GoldEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val addedAt: Long,
    val weightGrams: Double,
    val purchasePricePerGram: Long
)
