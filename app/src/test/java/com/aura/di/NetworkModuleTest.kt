package com.aura.di

import com.aura.data.network.ManageClient
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit

/**
 * Unit tests for the [NetworkModule] dependency injection methods.
 *
 * This class verifies that the network-related provider functions such as
 * [NetworkModule.provideRetrofit] and [NetworkModule.provideManageClient]
 * correctly create and configure instances needed for network communication.
 */
class NetworkModuleTest {

    /**
     * Tests that [NetworkModule.provideRetrofit] returns a properly configured [Retrofit] instance.
     *
     * Verifies that the returned Retrofit instance is not null and its base URL matches
     * the expected API URL defined in [com.aura.ui.ConstantsApp.API_URL].
     */
    @Test
    fun `test provideRetrofit returns configured Retrofit instance`() {
        // Act
        val retrofit: Retrofit = NetworkModule.provideRetrofit()

        // Assert
        assertNotNull(retrofit)
        assertEquals(retrofit.baseUrl().toString(), com.aura.ui.ConstantsApp.API_URL)
    }

    /**
     * Tests that [NetworkModule.provideManageClient] returns a [ManageClient] instance.
     *
     * Verifies that the ManageClient object created from the provided Retrofit instance is not null
     * and is of the correct type.
     */
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
