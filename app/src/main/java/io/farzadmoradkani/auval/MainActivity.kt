package io.farzadmoradkani.auval

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.farzadmoradkani.auval.ui.Screen
import io.farzadmoradkani.auval.ui.components.AuValNavigationBar
import io.farzadmoradkani.auval.ui.home.HomeScreen
import io.farzadmoradkani.auval.ui.portfolio.PortfolioScreen
import io.farzadmoradkani.auval.ui.settings.SettingsScreen
import io.farzadmoradkani.auval.ui.theme.AuValTheme
import io.farzadmoradkani.auval.ui.transactions.TransactionsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuValTheme {
                AuValApp()
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun AuValApp() {

    var currentScreen by remember {
        mutableStateOf<Screen>(Screen.Home)
    }

    Scaffold(
        modifier = Modifier, containerColor = MaterialTheme.colorScheme.background,
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

                Screen.Home -> HomeScreen()

                Screen.Portfolio -> PortfolioScreen()

                Screen.Transactions -> TransactionsScreen()

                Screen.Settings -> SettingsScreen()
            }
        }
    }
}

