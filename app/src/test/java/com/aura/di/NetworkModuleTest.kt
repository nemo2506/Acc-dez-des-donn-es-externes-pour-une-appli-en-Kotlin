package com.aura.di

import com.aura.data.network.ManageClient
import org.junit.Assert.*

import org.junit.Test
import retrofit2.Retrofit

class NetworkModuleTest {

    @Test
    fun `test provideRetrofit returns configured Retrofit instance`() {
        // Act
        val retrofit: Retrofit = NetworkModule.provideRetrofit()

        // Assert
        assertNotNull(retrofit)
        assertEquals(retrofit.baseUrl().toString(), com.aura.ui.ConstantsApp.API_URL)
    }

    @Test
    fun `test provideManageClient returns ManageClient`() {
        // Arrange
        val retrofit = NetworkModule.provideRetrofit()

        // Act
        val manageClient: ManageClient = NetworkModule.provideManageClient(retrofit)

        // Assert
        assertNotNull(manageClient)
        assertTrue(manageClient is ManageClient)
    }
}