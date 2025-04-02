package com.aura.di

import com.aura.data.network.ManageClient
import com.aura.data.repository.BankRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideWeatherRepository(dataClient: ManageClient): BankRepository {
        return BankRepository(dataClient)
    }
}
