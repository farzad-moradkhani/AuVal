package io.farzadmoradkani.auval.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.farzadmoradkani.auval.R
import io.farzadmoradkani.auval.data.repository.Gold18PriceRepository
import io.farzadmoradkani.auval.ui.components.BasicCard
import io.farzadmoradkani.auval.ui.components.LargeTitle
import io.farzadmoradkani.auval.util.extentions.separate3Digits
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    goldPriceSource: GoldPriceSource,
    onGoldPriceSourceSelected: (GoldPriceSource) -> Unit,
    goldPriceRepository: Gold18PriceRepository
) {
    var languageMenuExpanded by remember { mutableStateOf(false) }
    var sourceTestResults by remember {
        mutableStateOf<Map<GoldPriceSource, SourceTestResult>>(emptyMap())
    }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 15.dp)
            .padding(top = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            fontSize = 23.sp,
            fontWeight = FontWeight.SemiBold
        )

        BasicCard {
            LargeTitle(stringResource(R.string.settings_language_title))
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { languageMenuExpanded = true }
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(currentLanguage.labelRes),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "▾",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = languageMenuExpanded,
                    onDismissRequest = { languageMenuExpanded = false }
                ) {
                    AppLanguage.entries.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(stringResource(language.labelRes)) },
                            onClick = {
                                onLanguageSelected(language)
                                languageMenuExpanded = false
                            },
                            trailingIcon = {
                                if (language == currentLanguage) {
                                    Text(
                                        text = "✓",
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        BasicCard {
            LargeTitle(stringResource(R.string.settings_gold_source_title))
            Text(
                text = stringResource(R.string.settings_gold_source_note),
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                GoldPriceSource.entries.forEach { source ->
                    val testResult = sourceTestResults[source]

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onGoldPriceSourceSelected(source) }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = goldPriceSource == source,
                                onClick = null
                            )
                            Text(
                                text = stringResource(source.labelRes),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp),
                                fontSize = 14.sp
                            )
                            TextButton(
                                onClick = {
                                    sourceTestResults = sourceTestResults +
                                        (source to SourceTestResult.Loading)
                                    scope.launch {
                                        val result = goldPriceRepository.testSource(source)
                                        sourceTestResults = sourceTestResults + (
                                            source to result.fold(
                                                onSuccess = { SourceTestResult.Success(it) },
                                                onFailure = { SourceTestResult.Failure }
                                            )
                                        )
                                    }
                                },
                                enabled = testResult !is SourceTestResult.Loading
                            ) {
                                Text(
                                    text = if (testResult is SourceTestResult.Loading) {
                                        stringResource(R.string.settings_gold_source_testing)
                                    } else {
                                        stringResource(R.string.settings_gold_source_test)
                                    }
                                )
                            }
                        }

                        when (testResult) {
                            is SourceTestResult.Success -> Text(
                                text = stringResource(
                                    R.string.settings_gold_source_test_success,
                                    testResult.pricePerGram.separate3Digits()
                                ),
                                modifier = Modifier.padding(start = 40.dp, bottom = 4.dp),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.tertiary
                            )

                            SourceTestResult.Failure -> Text(
                                text = stringResource(R.string.settings_gold_source_test_failure),
                                modifier = Modifier.padding(start = 40.dp, bottom = 4.dp),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error
                            )

                            SourceTestResult.Loading, null -> Unit
                        }
                    }
                }
            }
        }
    }
}

private sealed interface SourceTestResult {
    data object Loading : SourceTestResult
    data class Success(val pricePerGram: Long) : SourceTestResult
    data object Failure : SourceTestResult
}
