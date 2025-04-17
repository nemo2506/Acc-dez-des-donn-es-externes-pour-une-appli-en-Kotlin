package com.aura.di

import com.aura.data.network.ManageClient
import com.aura.data.repository.BankRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides dependencies related to the data layer,
 * such as repositories and API clients.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    /**
     * Provides a singleton instance of [BankRepository] by injecting [ManageClient].
     *
     * @param dataClient The API client used for managing bank operations.
     * @return A singleton [BankRepository] instance.
     */
    @Singleton
    @Provides
    fun provideBankRepository(
        dataClient: ManageClient
    ): BankRepository {
        return BankRepository(dataClient)
    }
}
