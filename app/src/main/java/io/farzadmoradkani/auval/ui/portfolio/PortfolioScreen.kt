package io.farzadmoradkani.auval.ui.portfolio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.farzadmoradkani.auval.R
import io.farzadmoradkani.auval.data.repository.Gold18PriceRepository
import io.farzadmoradkani.auval.data.repository.GoldEntryRepository
import io.farzadmoradkani.auval.ui.components.BasicCard
import io.farzadmoradkani.auval.ui.components.LargeTitle
import io.farzadmoradkani.auval.ui.components.SmallTitle
import io.farzadmoradkani.auval.util.extentions.separate3Digits
import io.farzadmoradkani.auval.util.extentions.toCompactString
import io.farzadmoradkani.auval.util.extentions.toWeightString
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")

@Composable
fun PortfolioScreen(
    goldEntryRepository: GoldEntryRepository,
    goldPriceRepository: Gold18PriceRepository
) {
    val portfolioViewModel: PortfolioViewModel = remember(goldEntryRepository, goldPriceRepository) {
        PortfolioViewModel(goldEntryRepository, goldPriceRepository)
    }

    //viewmodel vars
    val holdings = portfolioViewModel.holdings.collectAsStateWithLifecycle()
    val totalWeight = remember(holdings.value) { holdings.value.sumOf { it.weightGrams } }
    val totalWorth = remember(holdings.value) { holdings.value.sumOf { it.valueToman } }

    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp)
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BasicCard {
                LargeTitle(stringResource(R.string.screen_portfolio_topCard_title))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        SmallTitle(stringResource(R.string.screen_portfolio_topCard_worth_title))
                        Text(
                            text = totalWorth.toCompactString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        SmallTitle(stringResource(R.string.currency_toman))
                    }
                    Column(horizontalAlignment = Alignment.Start) {
                        SmallTitle(stringResource(R.string.screen_portfolio_topCard_weight_title))
                        Text(
                            text = "${totalWeight.toWeightString()}g",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        SmallTitle(
                            "${holdings.value.size} ${
                                stringResource(R.string.screen_portfolio_topCard_deposit_footer)
                            }"
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                if (holdings.value.isEmpty()) {
                    item {
                        BasicCard {
                            Text(
                                text = stringResource(R.string.screen_portfolio_empty),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                } else {
                    items(holdings.value, key = { it.id }) { holding ->
                        HoldingCard(
                            holding = holding,
                            onRemove = { portfolioViewModel.removeHolding(holding.id) }
                        )
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(15.dp)
        ) {
            Text(stringResource(R.string.screen_portfolio_fab_add))
        }
    }

    if (showAddDialog) {
        AddHoldingDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { addedAt, weightGrams, purchasePricePerGram ->
                portfolioViewModel.addHolding(addedAt, weightGrams, purchasePricePerGram)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun HoldingCard(
    holding: PortfolioHolding,
    onRemove: () -> Unit
) {
    BasicCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                SmallTitle(stringResource(R.string.screen_portfolio_holding_added_at))
                Text(
                    text = holding.addedAt.format(dateTimeFormatter),
                    fontSize = 13.sp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${holding.weightGrams.toWeightString()}g",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = onRemove) {
                    Text(
                        text = stringResource(R.string.action_remove),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        PortfolioDetailRow(
            label = stringResource(R.string.screen_portfolio_holding_bought_price_title),
            value = "${holding.purchasePricePerGram.separate3Digits()} ${
                stringResource(R.string.screen_home_goldPriceCard_price_info)
            }"
        )
        PortfolioDetailRow(
            label = stringResource(R.string.screen_portfolio_holding_value_title),
            value = "${holding.valueToman.separate3Digits()} ${
                stringResource(R.string.currency_toman)
            }"
        )
        PortfolioDetailRow(
            label = stringResource(R.string.screen_portfolio_holding_profit_title),
            value = "${if (holding.profitToman >= 0) "+" else "-"}${
                abs(holding.profitToman).separate3Digits()
            } ${stringResource(R.string.currency_toman)}",
            valueColor = if (holding.profitToman >= 0) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    }
}

@Composable
private fun PortfolioDetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SmallTitle(label)
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

@Composable
private fun AddHoldingDialog(
    onDismiss: () -> Unit,
    onAdd: (addedAt: LocalDateTime?, weightGrams: Double, purchasePricePerGram: Long) -> Unit
) {
    var dateTime by remember { mutableStateOf(LocalDateTime.now().format(dateTimeFormatter)) }
    var weight by remember { mutableStateOf("") }
    var boughtPrice by remember { mutableStateOf("") }

    val weightValue = weight.toDoubleOrNull()
    val boughtPriceValue = boughtPrice.filter { it.isDigit() }.toLongOrNull()
    val isValid = weightValue != null && weightValue > 0 &&
        boughtPriceValue != null && boughtPriceValue > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.screen_portfolio_addDialog_title)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = dateTime,
                    onValueChange = { dateTime = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(stringResource(R.string.screen_portfolio_addDialog_dateTime_label))
                    },
                    singleLine = true
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { input -> weight = sanitizeWeightInput(input) },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(stringResource(R.string.screen_portfolio_addDialog_weight_label))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = boughtPrice,
                    onValueChange = { input ->
                        boughtPrice = formatThousands(
                            normalizeDigits(input).filter { it.isDigit() }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(stringResource(R.string.screen_portfolio_addDialog_purchasePrice_label))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedWeight = weightValue
                    val parsedBoughtPrice = boughtPriceValue
                    if (parsedWeight != null && parsedBoughtPrice != null) {
                        onAdd(
                            dateTime.toLocalDateTimeOrNull(),
                            parsedWeight,
                            parsedBoughtPrice
                        )
                    }
                },
                enabled = isValid
            ) {
                Text(stringResource(R.string.action_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

private fun String.toLocalDateTimeOrNull(): LocalDateTime? =
    trim()
        .takeIf { it.isNotEmpty() }
        ?.let { value -> runCatching { LocalDateTime.parse(value, dateTimeFormatter) }.getOrNull() }

private fun sanitizeWeightInput(input: String): String {
    val normalized = normalizeDigits(input).replace('٫', '.').replace(',', '.')
    val filtered = normalized.filter { it.isDigit() || it == '.' }
    val firstDot = filtered.indexOf('.')
    if (firstDot == -1) return filtered

    return filtered.substring(0, firstDot + 1) +
        filtered.substring(firstDot + 1).replace(".", "")
}

private fun normalizeDigits(input: String): String = buildString(input.length) {
    input.forEach { character ->
        val digit = when (character) {
            in '۰'..'۹' -> character - '۰'
            in '٠'..'٩' -> character - '٠'
            else -> null
        }
        append(if (digit != null) '0' + digit else character)
    }
}

private fun formatThousands(digits: String): String {
    if (digits.isEmpty()) return ""

    return digits.reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}
