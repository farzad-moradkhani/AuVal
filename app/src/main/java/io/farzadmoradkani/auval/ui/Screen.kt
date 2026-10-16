package io.farzadmoradkani.auval.ui

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import io.farzadmoradkani.auval.R


sealed class Screen(
    val route: String,
    val title: Int,
    val iconId: Int
) {
    data object Home : Screen(
        "home",
        R.string.bottomNav_title_home,
        R.drawable.home_icon
    )

    data object Portfolio : Screen(
        "portfolio",
        R.string.bottomNav_title_portfolio,
        R.drawable.wallet_icon
    )

    data object Transactions : Screen(
        "transactions",
        R.string.bottomNav_title_transactions,
        R.drawable.transaction_icon
    )

    data object Settings : Screen(
        "settings",
        R.string.bottomNav_title_settings,
        R.drawable.settings_icon
    )
}