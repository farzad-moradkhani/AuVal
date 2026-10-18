package io.farzadmoradkani.auval.util.extentions

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun Number.separate3Digits():String{
    return NumberFormat.getNumberInstance(Locale.US).format(this)
}

fun Number.toWeightString(): String =
    BigDecimal.valueOf(toDouble())
        .stripTrailingZeros()
        .toPlainString()


fun Number.toCompactString(): String {
    val value = toDouble()

    return when {
        value >= 1_000_000_000 ->
            "${formatCompact(value / 1_000_000_000)}B"

        value >= 1_000_000 ->
            "${formatCompact(value / 1_000_000)}M"

        value >= 1_000 ->
            "${formatCompact(value / 1_000)}K"

        else -> toString()
    }
}

private fun formatCompact(value: Double): String =
    if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        String.format(java.util.Locale.US, "%.1f", value)
            .trimEnd('0')
            .trimEnd('.')
    }