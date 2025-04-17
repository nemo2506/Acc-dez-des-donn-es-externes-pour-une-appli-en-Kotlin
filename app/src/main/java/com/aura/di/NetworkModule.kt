package com.aura.di

import com.aura.data.network.ManageClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Hilt module that provides network-related dependencies such as Retrofit,
 * OkHttpClient, and API service interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides a singleton instance of [Retrofit] configured with a base URL,
     * Moshi converter, and an [OkHttpClient] with logging.
     *
     * @return A configured [Retrofit] instance for API calls.
     */
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.17:8080")
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .client(provideOkHttpClient())
            .build()
    }

    /**
     * Provides a singleton instance of [ManageClient], the API interface used for
     * accessing endpoints related to user login, balance, and transfers.
     *
     * @param retrofit The [Retrofit] instance used to create the API service.
     * @return An implementation of [ManageClient].
     */
    @Singleton
    @Provides
    fun provideManageClient(retrofit: Retrofit): ManageClient {
        return retrofit.create(ManageClient::class.java)
    }

    /**
     * Configures and returns an [OkHttpClient] with an HTTP logging interceptor.
     * This function is private because it's only used internally within this module.
     *
     * @return A configured [OkHttpClient] instance.
     */
    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }.build()
    }
}
