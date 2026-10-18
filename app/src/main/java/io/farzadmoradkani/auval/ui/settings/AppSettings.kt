package io.farzadmoradkani.auval.ui.settings

import android.content.Context
import androidx.annotation.StringRes
import io.farzadmoradkani.auval.R
import java.util.Locale

enum class AppLanguage(
    val languageTag: String?,
    @param:StringRes val labelRes: Int
) {
    SYSTEM(null, R.string.settings_language_system),
    ENGLISH("en", R.string.language_english),
    PERSIAN("fa", R.string.language_persian),
    ARABIC("ar", R.string.language_arabic),
    FRENCH("fr", R.string.language_french),
    GERMAN("de", R.string.language_german),
    CHINESE("zh", R.string.language_chinese);

    fun resolveLocale(systemLocale: Locale): Locale =
        languageTag?.let(Locale::forLanguageTag) ?: systemLocale
}

enum class GoldPriceSource(@param:StringRes val labelRes: Int) {
    IRANJIB(R.string.settings_gold_source_iranjib),
    TALASEA(R.string.settings_gold_source_talasea),
    TGJU(R.string.settings_gold_source_tgju)
}

private const val PREFS_NAME = "auval_settings"
private const val KEY_LANGUAGE = "app_language"
private const val KEY_GOLD_SOURCE = "gold_price_source"

fun loadAppLanguage(context: Context): AppLanguage {
    val stored = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getString(KEY_LANGUAGE, null)
    return AppLanguage.entries.firstOrNull { it.name == stored } ?: AppLanguage.SYSTEM
}

fun saveAppLanguage(context: Context, language: AppLanguage) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_LANGUAGE, language.name)
        .apply()
}

fun loadGoldPriceSource(context: Context): GoldPriceSource {
    val stored = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getString(KEY_GOLD_SOURCE, null)
    return GoldPriceSource.entries.firstOrNull { it.name == stored } ?: GoldPriceSource.TGJU
}

fun saveGoldPriceSource(context: Context, source: GoldPriceSource) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_GOLD_SOURCE, source.name)
        .apply()
}
