package com.xbot.currencyconverter.ui.feature.result

sealed interface ResultScreenState {
    data object Loading: ResultScreenState

    data class Success(val result: String): ResultScreenState
}
