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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.farzadmoradkani.auval.R
import io.farzadmoradkani.auval.util.extentions.separate3Digits
import io.farzadmoradkani.auval.util.extentions.toCompactString
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    val homeViewModel = HomeViewModel()

    //viewmodel vars
    val greetingTextId = homeViewModel.greetingsTextId.collectAsStateWithLifecycle()
    val gold18Price = homeViewModel.goldPrice18.collectAsStateWithLifecycle()
    val lastUpdate = homeViewModel.lastUpdate.collectAsStateWithLifecycle()
    val totalGoldWeight = homeViewModel.totalGoldWeight.collectAsStateWithLifecycle();
    val totalGoldWeightWorth = homeViewModel.totalGoldWeightWorth.collectAsStateWithLifecycle()
    val totalDepositCount = homeViewModel.totalDepositCount.collectAsStateWithLifecycle()
    val oneDayPnLChange = homeViewModel.oneDayPnLChange.collectAsStateWithLifecycle()
    val totalPnl = homeViewModel.totalPnl.collectAsStateWithLifecycle()

    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()


    homeViewModel.updateGreetingText()
    Column(
        modifier = Modifier
            .fillMaxSize()
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
            Column(horizontalAlignment = Alignment.End) {
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        text = stringResource(R.string.screen_home_goldPriceCard_title),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        text = gold18Price.value.separate3Digits(),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
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
                                scope.launch {
                                    rotation.snapTo(0f)
                                    rotation.animateTo(
                                        targetValue = 1080f,
                                        animationSpec = tween(
                                            durationMillis = 3000,
                                            easing = LinearEasing
                                        )
                                    )

                                    //TODO: Update price here

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
                            text = "${lastUpdate.value} ${stringResource(R.string.screen_home_goldPriceCard_liveData_update)}",
                            color = MaterialTheme.colorScheme.secondary
                        )

                    }
                }


            }
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
                Column(modifier = Modifier, horizontalAlignment = Alignment.End) {
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
                Column(modifier = Modifier, horizontalAlignment = Alignment.End) {
                    Text(
                        stringResource(R.string.screen_home_portfolio_myGold_title),
                        fontSize = 12.sp
                    )
                    Text(
                        "${totalGoldWeight.value}g", fontWeight = FontWeight.Bold,
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
        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(stringResource(R.string.screen_home_bottomCards_title_today))
                Text(
                    modifier = Modifier.padding(end = 20.dp),
                    fontWeight = FontWeight.SemiBold,
                    text = if (oneDayPnLChange.value > 0) "+${oneDayPnLChange.value.separate3Digits()}" else
                        "-${oneDayPnLChange.value.separate3Digits()}", fontSize = 30.sp
                )
                Text(stringResource(R.string.currency_toman))

            }

        }

        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(stringResource(R.string.screen_home_bottomCards_title_totalPnL))
                Text(
                    modifier = Modifier.padding(end = 20.dp),
                    fontWeight = FontWeight.SemiBold,
                    text = if (oneDayPnLChange.value > 0) "+${totalPnl.value.separate3Digits()}" else
                        "-${totalPnl.value.separate3Digits()}", fontSize = 30.sp
                )
                Text(stringResource(R.string.currency_toman))

            }

        }
    }


}