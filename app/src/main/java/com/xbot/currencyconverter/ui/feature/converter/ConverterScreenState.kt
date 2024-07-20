package com.xbot.currencyconverter.ui.feature.converter

import kotlinx.collections.immutable.ImmutableList

sealed interface ConverterScreenState {
    data object Loading: ConverterScreenState

    data class Success(val symbols: ImmutableList<String>): ConverterScreenState

    data class Error(val message: String): ConverterScreenState
}

sealed interface ConverterScreenEvent {
    data object Load: ConverterScreenEvent

    data class Convert(
        val amount: Double,
        val sourceCurrency: String,
        val targetCurrency: String,
        val onResult: (String) -> Unit
    ): ConverterScreenEvent
}
