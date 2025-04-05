package com.aura.di

import android.content.Context
import com.aura.data.network.ManageClient
import com.aura.data.repository.BankRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideBankRepository(
        dataClient: ManageClient,
        @ApplicationContext context: Context
    ): BankRepository {
        return BankRepository(dataClient, context)
    }
}
