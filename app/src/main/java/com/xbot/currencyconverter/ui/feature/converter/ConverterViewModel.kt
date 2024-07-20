package com.xbot.currencyconverter.ui.feature.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xbot.domain.model.Resource
import com.xbot.domain.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _state: MutableStateFlow<ConverterScreenState> =
        MutableStateFlow(ConverterScreenState.Loading)
    val state: StateFlow<ConverterScreenState> = _state.asStateFlow()

    fun onEvent(event: ConverterScreenEvent) {
        when (event) {
            is ConverterScreenEvent.Load -> fetchSymbols()

            is ConverterScreenEvent.Convert -> {
                convert(event.sourceCurrency, event.targetCurrency, event.amount, event.onResult)
            }
        }
    }

    private fun fetchSymbols() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { ConverterScreenState.Loading }
            repository.symbols.collect {
                val state = when (it) {
                    is Resource.Success -> ConverterScreenState.Success(it.data.toPersistentList())

                    is Resource.Error -> ConverterScreenState.Error(it.message)
                }
                _state.update { state }
            }
        }
    }

    private fun convert(
        sourceCurrency: String,
        targetCurrency: String,
        amount: Double,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { ConverterScreenState.Loading }
            repository.convert(sourceCurrency, targetCurrency, amount).collect {
                when (it) {
                    is Resource.Success -> {
                        withContext(Dispatchers.Main) {
                            onResult(it.data)
                        }
                    }

                    is Resource.Error -> {
                        val state = ConverterScreenState.Error(it.message)
                        _state.update { state }
                    }
                }
            }
        }
    }
}