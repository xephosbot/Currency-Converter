package com.xbot.domain.repository

import com.xbot.domain.model.Resource
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {

    val symbols: Flow<Resource<List<String>>>

    fun convert(
        sourceCurrency: String,
        targetCurrency: String,
        amount: Double
    ): Flow<Resource<String>>
}