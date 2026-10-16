package io.farzadmoradkani.auval.ui.home

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.farzadmoradkani.auval.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel(){

    private val _greetingsTextId = MutableStateFlow<Int>(getGreetingString(getHourOfTheDay()))
    val greetingsTextId: StateFlow<Int> = _greetingsTextId.asStateFlow()


    private val _goldPrice18 = MutableStateFlow<Long>(26500000)
    val goldPrice18: StateFlow<Long> = _goldPrice18.asStateFlow()


    private val _lastUpdate = MutableStateFlow<String>(getTimeInFormat())
    val lastUpdate: StateFlow<String> = _lastUpdate.asStateFlow()

    private val _totalGoldWeight = MutableStateFlow<Long>(0)
    val totalGoldWeight : StateFlow<Long> = _totalGoldWeight.asStateFlow()

//    private val _totalGoldWorth
    private val _totalGoldWeightWorth = MutableStateFlow<Long>(260000000)
    val totalGoldWeightWorth : StateFlow<Long> = _totalGoldWeightWorth.asStateFlow()

    private val _totalDepositCount = MutableStateFlow<Int>(0)
    val totalDepositCount : StateFlow<Int> = _totalDepositCount.asStateFlow()

    private val _oneDayPnLChange = MutableStateFlow<Long>(100000)
    val oneDayPnLChange : StateFlow<Long> = _oneDayPnLChange.asStateFlow()

    private val _totalPnl = MutableStateFlow<Long>(500000)
    val totalPnl : StateFlow<Long> = _totalPnl.asStateFlow()



    fun updateGreetingText(){
        viewModelScope.launch {
            _greetingsTextId.value=getGreetingString(getHourOfTheDay())
        }
    }
}
fun getGreetingString(hour: Int): Int{
    return when(hour){
        in 5..11 -> R.string.screen_home_header_greetings_morning
        in 12 .. 17 -> R.string.screen_home_header_greetings_afternoon
        in 18..21 -> R.string.screen_home_header_greetings_evening
        else -> R.string.screen_home_header_greetings_night
    }
}
fun getHourOfTheDay():Int{
    return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
}
fun getTimeInFormat(): String{
    return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
}


