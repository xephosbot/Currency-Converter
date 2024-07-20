package com.xbot.data.di

import com.skydoves.retrofit.adapters.result.ResultCallAdapterFactory
import com.xbot.data.Constants
import com.xbot.data.api.ExchangeRateApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Singleton
    @Provides
    fun provideExchangeRateApi(): ExchangeRateApi {
        val contentType = "application/json; charset=UTF8".toMediaType()
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .addCallAdapterFactory(ResultCallAdapterFactory.create())
            .build()
            .create(ExchangeRateApi::class.java)
    }
}
