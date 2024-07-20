package com.xbot.currencyconverter.ui.feature.converter

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xbot.currencyconverter.R
import com.xbot.currencyconverter.ui.component.Scaffold
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = hiltViewModel(),
    navigateToResult: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(ConverterScreenEvent.Load)
    }

    ConverterScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        navigateToResult = navigateToResult
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConverterScreenContent(
    modifier: Modifier = Modifier,
    state: ConverterScreenState,
    onEvent: (ConverterScreenEvent) -> Unit,
    navigateToResult: (String) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
                }
            )
        }
    ) { contentPadding ->
        Crossfade(targetState = state, label = "") { state ->
            when (state) {
                is ConverterScreenState.Loading -> {
                    LoadingScreen(
                        modifier = Modifier.padding(contentPadding)
                    )
                }

                is ConverterScreenState.Error -> {
                    ErrorScreen(
                        modifier = Modifier.padding(contentPadding),
                        message = state.message,
                        onRetry = { onEvent(ConverterScreenEvent.Load) }
                    )
                }

                is ConverterScreenState.Success -> {
                    SuccessScreen(
                        modifier = Modifier.padding(contentPadding),
                        symbols = state.symbols,
                        onResult = { from, to, amount ->
                            onEvent(
                                ConverterScreenEvent.Convert(
                                    sourceCurrency = from,
                                    targetCurrency = to,
                                    amount = amount,
                                    onResult = navigateToResult
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SuccessScreen(
    modifier: Modifier = Modifier,
    symbols: ImmutableList<String>,
    onResult: (String, String, Double) -> Unit
) {
    var amount by rememberSaveable { mutableStateOf("") }
    var sourceCurrency by rememberSaveable { mutableStateOf(symbols.first()) }
    var targetCurrency by rememberSaveable { mutableStateOf(symbols.first()) }

    val pattern = remember { Regex("^\\d+\$") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = amount,
                onValueChange = {
                    if (it.isEmpty() || it.matches(pattern)) {
                        amount = it
                    }
                },
                label = {
                    Text(
                        text = "Amount",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                ExposedNotEditableDropdownMenu(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    items = symbols,
                    selected = sourceCurrency,
                    onItemSelected = {
                        sourceCurrency = it
                    },
                    label = {
                        Text(
                            text = "Source currency",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
                IconButton(
                    onClick = {
                        val temp = sourceCurrency
                        sourceCurrency = targetCurrency
                        targetCurrency = temp
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.SyncAlt,
                        contentDescription = ""
                    )
                }
                ExposedNotEditableDropdownMenu(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    items = symbols,
                    selected = targetCurrency,
                    onItemSelected = {
                        targetCurrency = it
                    },
                    label = {
                        Text(
                            text = "Target currency",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                onClick = {
                    amount.toDoubleOrNull()?.let { amount ->
                        onResult(sourceCurrency, targetCurrency, amount)
                    }
                }
            ) {
                Text(text = "Result")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedNotEditableDropdownMenu(
    modifier: Modifier = Modifier,
    items: ImmutableList<String>,
    selected: String,
    label: @Composable (() -> Unit)? = null,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            value = selected,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = label,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorScreen(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(
                onClick = onRetry
            ) {
                Text(text = "Retry")
            }
        }
    }
}
