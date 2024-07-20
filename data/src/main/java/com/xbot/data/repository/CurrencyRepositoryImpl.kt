package com.xbot.data.repository

import com.xbot.data.Constants
import com.xbot.data.api.ExchangeRateApi
import com.xbot.data.map
import com.xbot.data.safeApiCall
import com.xbot.data.toDecimalNotationString
import com.xbot.domain.model.Resource
import com.xbot.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.math.round

class CurrencyRepositoryImpl @Inject constructor(
    private val api: ExchangeRateApi
) : CurrencyRepository {

    override val symbols: Flow<Resource<List<String>>> = safeApiCall {
        api.getSymbols(Constants.API_KEY)
    }.map {
        it.symbols.keys.toList()
    }

    override fun convert(
        sourceCurrency: String,
        targetCurrency: String,
        amount: Double
    ): Flow<Resource<String>> = safeApiCall {
        api.getRates(Constants.API_KEY)
    }.map {
        val source = it.rates.getValue(sourceCurrency)
        val target = it.rates.getValue(targetCurrency)
        val rate = target / source
        val convertedAmount = round(amount * rate * 100) / 100
        "${amount.toDecimalNotationString()} $sourceCurrency = ${convertedAmount.toDecimalNotationString()} $targetCurrency"
    }
}