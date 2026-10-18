package io.farzadmoradkani.auval.ui.home

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.farzadmoradkani.auval.R
import io.farzadmoradkani.auval.data.repository.Gold18PriceRepository
import io.farzadmoradkani.auval.data.repository.GoldEntryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToLong

class HomeViewModel(
    private val goldPriceRepository: Gold18PriceRepository,
    goldEntryRepository: GoldEntryRepository
) : ViewModel() {

    private val _greetingsTextId = MutableStateFlow(getGreetingString(getHourOfTheDay()))
    val greetingsTextId: StateFlow<Int> = _greetingsTextId.asStateFlow()

    private val entries = goldEntryRepository.entries
    private val priceState = goldPriceRepository.state

    val goldPrice18: StateFlow<Long> = priceState
        .map { it.pricePerGram }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    val lastUpdate: StateFlow<String> = priceState
        .map { state -> state.lastUpdated?.let(::formatTime).orEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    val isRefreshing: StateFlow<Boolean> = priceState
        .map { it.isLoading }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val priceError: StateFlow<String?> = priceState
        .map { it.error }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val totalGoldWeight: StateFlow<Double> = entries
        .map { list -> list.sumOf { it.weightGrams } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val totalDepositCount: StateFlow<Int> = entries
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val totalCost: StateFlow<Long> = entries
        .map { list -> list.sumOf { (it.weightGrams * it.purchasePricePerGram).roundToLong() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    val totalGoldWeightWorth: StateFlow<Long> =
        combine(entries, priceState) { list, price ->
            if (price.pricePerGram <= 0L) {
                0L
            } else {
                list.sumOf { it.weightGrams * price.pricePerGram }.roundToLong()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    val totalPnl: StateFlow<Long> =
        combine(totalGoldWeightWorth, totalCost) { worth, cost ->
            if (worth <= 0L) 0L else worth - cost
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    // Price history is not stored yet, so today's change remains zero for this MVP.
    val oneDayPnLChange: StateFlow<Long> = MutableStateFlow(0L)

    fun updateGreetingText() {
        _greetingsTextId.value = getGreetingString(getHourOfTheDay())
    }

    fun refresh() {
        goldPriceRepository.refresh()
    }
}

private fun formatTime(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
        .format(DateTimeFormatter.ofPattern("HH:mm"))

fun getGreetingString(hour: Int): Int {
    return when (hour) {
        in 5..11 -> R.string.screen_home_header_greetings_morning
        in 12..17 -> R.string.screen_home_header_greetings_afternoon
        in 18..21 -> R.string.screen_home_header_greetings_evening
        else -> R.string.screen_home_header_greetings_night
    }
}

fun getHourOfTheDay(): Int {
    return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
}
