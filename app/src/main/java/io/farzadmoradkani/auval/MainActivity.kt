package io.farzadmoradkani.auval

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.LocaleList
import android.text.TextUtils
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.LayoutDirection
import io.farzadmoradkani.auval.data.repository.Gold18PriceRepository
import io.farzadmoradkani.auval.data.repository.GoldEntryRepository
import io.farzadmoradkani.auval.ui.Screen
import io.farzadmoradkani.auval.ui.components.AuValNavigationBar
import io.farzadmoradkani.auval.ui.home.HomeScreen
import io.farzadmoradkani.auval.ui.portfolio.PortfolioScreen
import io.farzadmoradkani.auval.ui.settings.AppLanguage
import io.farzadmoradkani.auval.ui.settings.GoldPriceSource
import io.farzadmoradkani.auval.ui.settings.SettingsScreen
import io.farzadmoradkani.auval.ui.settings.loadAppLanguage
import io.farzadmoradkani.auval.ui.settings.loadGoldPriceSource
import io.farzadmoradkani.auval.ui.settings.saveAppLanguage
import io.farzadmoradkani.auval.ui.settings.saveGoldPriceSource
import io.farzadmoradkani.auval.ui.theme.AuValTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as AuValApplication

        setContent {
            var appLanguage by remember { mutableStateOf(loadAppLanguage(this)) }
            var goldPriceSource by remember { mutableStateOf(loadGoldPriceSource(this)) }

            val systemLocale = resources.configuration.locales[0]
            val locale = appLanguage.resolveLocale(systemLocale)
            val localizedContext = remember(appLanguage, systemLocale) {
                createLocalizedContext(this, appLanguage)
            }
            val layoutDirection = remember(locale) {
                if (TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_RTL) {
                    LayoutDirection.Rtl
                } else {
                    LayoutDirection.Ltr
                }
            }

            LaunchedEffect(goldPriceSource) {
                app.goldPriceRepository.selectSource(goldPriceSource)
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalResources provides localizedContext.resources,
                LocalConfiguration provides localizedContext.resources.configuration,
                LocalLayoutDirection provides layoutDirection
            ) {
                AuValTheme {
                    AuValApp(
                        goldPriceRepository = app.goldPriceRepository,
                        goldEntryRepository = app.goldEntryRepository,
                        currentLanguage = appLanguage,
                        onLanguageSelected = { language ->
                            saveAppLanguage(this, language)
                            appLanguage = language
                        },
                        goldPriceSource = goldPriceSource,
                        onGoldPriceSourceSelected = { source ->
                            saveGoldPriceSource(this, source)
                            goldPriceSource = source
                        }
                    )
                }
            }
        }
    }
}

private fun createLocalizedContext(context: Context, language: AppLanguage): Context {
    val baseConfiguration = context.resources.configuration
    val locale = language.resolveLocale(baseConfiguration.locales[0])
    val configuration = Configuration(baseConfiguration).apply {
        setLocale(locale)
        setLocales(LocaleList(locale))
    }
    return context.createConfigurationContext(configuration)
}

@Composable
fun AuValApp(
    goldPriceRepository: Gold18PriceRepository,
    goldEntryRepository: GoldEntryRepository,
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    goldPriceSource: GoldPriceSource,
    onGoldPriceSourceSelected: (GoldPriceSource) -> Unit
) {
    var currentScreen by remember {
        mutableStateOf<Screen>(Screen.Portfolio)
    }

    Scaffold(
        modifier = Modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AuValNavigationBar(
                currentScreen = currentScreen,
                onScreenSelected = { screen ->
                    currentScreen = screen
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Home -> HomeScreen(
                    goldPriceRepository = goldPriceRepository,
                    goldEntryRepository = goldEntryRepository
                )

                Screen.Portfolio -> PortfolioScreen(
                    goldEntryRepository = goldEntryRepository,
                    goldPriceRepository = goldPriceRepository
                )

                Screen.Settings -> SettingsScreen(
                    currentLanguage = currentLanguage,
                    onLanguageSelected = onLanguageSelected,
                    goldPriceSource = goldPriceSource,
                    onGoldPriceSourceSelected = onGoldPriceSourceSelected,
                    goldPriceRepository = goldPriceRepository
                )
            }
        }
    }
}
