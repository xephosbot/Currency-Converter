package com.xbot.data.di

import com.xbot.data.api.ExchangeRateApi
import com.xbot.data.repository.CurrencyRepositoryImpl
import com.xbot.domain.repository.CurrencyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideCurrencyRepository(api: ExchangeRateApi): CurrencyRepository {
        return CurrencyRepositoryImpl(api)
    }
}