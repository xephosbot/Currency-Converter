package com.xbot.data.api

import com.xbot.data.model.RatesResponseDto
import com.xbot.data.model.SymbolsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApi {
    @GET("/latest")
    suspend fun getRates(@Query("access_key") apikey: String): Result<RatesResponseDto>

    @GET("/symbols")
    suspend fun getSymbols(@Query("access_key") apikey: String): Result<SymbolsResponseDto>
}