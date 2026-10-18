package io.farzadmoradkani.auval.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.farzadmoradkani.auval.data.local.GoldEntryEntity
import io.farzadmoradkani.auval.data.repository.Gold18PriceRepository
import io.farzadmoradkani.auval.data.repository.GoldEntryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.roundToLong

data class PortfolioHolding(
    val id: Long,
    val addedAt: LocalDateTime,
    val weightGrams: Double,
    val purchasePricePerGram: Long,
    val valueToman: Long,
    val profitToman: Long
)

class PortfolioViewModel(
    private val goldEntryRepository: GoldEntryRepository,
    goldPriceRepository: Gold18PriceRepository
) : ViewModel() {

    val holdings: StateFlow<List<PortfolioHolding>> =
        combine(goldEntryRepository.entries, goldPriceRepository.state) { entries, price ->
            entries.map { entry ->
                val currentValue = if (price.pricePerGram > 0L) {
                    (entry.weightGrams * price.pricePerGram).roundToLong()
                } else {
                    0L
                }

                val purchasedValue = (entry.weightGrams * entry.purchasePricePerGram).roundToLong()
                PortfolioHolding(
                    id = entry.id,
                    addedAt = Instant.ofEpochMilli(entry.addedAt)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                    weightGrams = entry.weightGrams,
                    purchasePricePerGram = entry.purchasePricePerGram,
                    valueToman = currentValue,
                    profitToman = if (currentValue > 0L) {
                        currentValue - purchasedValue
                    } else {
                        0L
                    }
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addHolding(
        addedAt: LocalDateTime?,
        weightGrams: Double,
        purchasePricePerGram: Long
    ) {
        val dateTime = addedAt ?: LocalDateTime.now()
        viewModelScope.launch {
            goldEntryRepository.add(
                GoldEntryEntity(
                    addedAt = dateTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                    weightGrams = weightGrams,
                    purchasePricePerGram = purchasePricePerGram
                )
            )
        }
    }

    fun removeHolding(id: Long) {
        viewModelScope.launch {
            goldEntryRepository.remove(id)
        }
    }
}
