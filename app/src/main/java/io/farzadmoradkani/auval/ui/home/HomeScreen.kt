package io.farzadmoradkani.auval.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.farzadmoradkani.auval.R
import io.farzadmoradkani.auval.data.repository.Gold18PriceRepository
import io.farzadmoradkani.auval.data.repository.GoldEntryRepository
import io.farzadmoradkani.auval.util.extentions.separate3Digits
import io.farzadmoradkani.auval.util.extentions.toCompactString
import io.farzadmoradkani.auval.util.extentions.toWeightString
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs

@Composable
fun HomeScreen(
    goldPriceRepository: Gold18PriceRepository,
    goldEntryRepository: GoldEntryRepository
) {
    val homeViewModel: HomeViewModel = remember(goldPriceRepository, goldEntryRepository) {
        HomeViewModel(goldPriceRepository, goldEntryRepository)
    }

    //viewmodel vars
    val greetingTextId = homeViewModel.greetingsTextId.collectAsStateWithLifecycle()
    val gold18Price = homeViewModel.goldPrice18.collectAsStateWithLifecycle()
    val lastUpdate = homeViewModel.lastUpdate.collectAsStateWithLifecycle()
    val totalGoldWeight = homeViewModel.totalGoldWeight.collectAsStateWithLifecycle();
    val totalGoldWeightWorth = homeViewModel.totalGoldWeightWorth.collectAsStateWithLifecycle()
    val totalDepositCount = homeViewModel.totalDepositCount.collectAsStateWithLifecycle()
    val oneDayPnLChange = homeViewModel.oneDayPnLChange.collectAsStateWithLifecycle()
    val totalPnl = homeViewModel.totalPnl.collectAsStateWithLifecycle()
    val priceError = homeViewModel.priceError.collectAsStateWithLifecycle()

    val todayPnlPercent = remember(totalGoldWeightWorth.value, oneDayPnLChange.value) {
        val previousWorth = totalGoldWeightWorth.value - oneDayPnLChange.value
        if (previousWorth != 0L) oneDayPnLChange.value.toDouble() / previousWorth * 100 else null
    }
    val totalPnlPercent = remember(totalGoldWeightWorth.value, totalPnl.value) {
        val costBasis = totalGoldWeightWorth.value - totalPnl.value
        if (costBasis != 0L) totalPnl.value.toDouble() / costBasis * 100 else null
    }

    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        homeViewModel.updateGreetingText()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 15.dp)
            .padding(top = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        //Header
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box() {
                Text(
                    text = stringResource(R.string.app_name),
                    modifier = Modifier, fontSize = 23.sp, fontWeight = FontWeight.SemiBold
                )
            }
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    stringResource(greetingTextId.value),
                    modifier = Modifier, fontSize = 23.sp, fontWeight = FontWeight.SemiBold
                )
                Text(
                    stringResource(R.string.screen_home_header_secondaryText),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        //Gold Price Card
        Card(
            modifier = Modifier
                .fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = stringResource(R.string.screen_home_goldPriceCard_title),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = if (gold18Price.value > 0L) {
                            gold18Price.value.separate3Digits()
                        } else {
                            "..."
                        },
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = stringResource(R.string.screen_home_goldPriceCard_price_info),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        modifier = Modifier
                            .size(40.dp)
                            .graphicsLayer {
                                rotationZ = rotation.value
                            }
                            .clickable {
                                homeViewModel.refresh()
                                scope.launch {
                                    rotation.snapTo(0f)
                                    rotation.animateTo(
                                        targetValue = 1080f,
                                        animationSpec = tween(
                                            durationMillis = 3000,
                                            easing = LinearEasing
                                        )
                                    )
                                }
                            },
                        painter = painterResource(R.drawable.sync_icon),
                        contentDescription = "sync",

                        )
                    Card(
                        modifier = Modifier, shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .padding(2.dp),
                            text = if (lastUpdate.value.isBlank()) {
                                stringResource(R.string.screen_home_goldPriceCard_liveData_update)
                            } else {
                                "${lastUpdate.value} ${
                                    stringResource(R.string.screen_home_goldPriceCard_liveData_update)
                                }"
                            },
                            color = MaterialTheme.colorScheme.secondary
                        )

                    }
                }


            }
        }

        if (priceError.value != null) {
            Text(
                text = stringResource(R.string.screen_home_goldPriceCard_error),
                modifier = Modifier.padding(horizontal = 4.dp),
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp, horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceAround,

                ) {
                Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
                    Text(
                        stringResource(R.string.screen_home_portfolio_worth_title),
                        fontSize = 12.sp
                    )
                    Text(
                        totalGoldWeightWorth.value.toCompactString(), fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                    Text(
                        stringResource(R.string.screen_home_portfolio_worth_footer),
                        fontSize = 12.sp
                    )
                }
                Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
                    Text(
                        stringResource(R.string.screen_home_portfolio_myGold_title),
                        fontSize = 12.sp
                    )
                    Text(
                        "${totalGoldWeight.value.toWeightString()}g", fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                    Text(
                        "${totalDepositCount.value} ${stringResource(R.string.screen_home_portfolio_myGold_footer)}",
                        fontSize = 12.sp
                    )
                }
            }

        }
        //Bottom cards
        Text(
            text = stringResource(R.string.screen_home_bottomCards_title_performance),
            modifier = Modifier.padding(top = 5.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        PnlCard(
            title = stringResource(R.string.screen_home_bottomCards_title_today),
            subtitle = stringResource(R.string.screen_home_bottomCards_subtitle_today),
            amount = oneDayPnLChange.value,
            percent = todayPnlPercent
        )

        PnlCard(
            title = stringResource(R.string.screen_home_bottomCards_title_totalPnL),
            subtitle = stringResource(R.string.screen_home_bottomCards_subtitle_total),
            amount = totalPnl.value,
            percent = totalPnlPercent
        )
    }


}

@Composable
private fun PnlCard(
    title: String,
    subtitle: String,
    amount: Long,
    percent: Double?
) {
    val isPositive = amount >= 0
    val accentColor = if (isPositive) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.error
    }
    val sign = when {
        amount > 0 -> "+"
        amount < 0 -> "-"
        else -> ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                PnlChangeBadge(percent = percent, isPositive = isPositive)
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "$sign${abs(amount).separate3Digits()}",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = accentColor,
                textAlign = TextAlign.Start
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.currency_toman),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PnlChangeBadge(
    percent: Double?,
    isPositive: Boolean
) {
    val containerColor = if (isPositive) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = if (isPositive) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }
    val arrow = if (isPositive) "▲" else "▼"
    val percentText = percent?.let {
        " ${String.format(Locale.US, "%.2f", abs(it))}%"
    } ?: ""

    Box(
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = "$arrow$percentText",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
    }
}