package com.aura.di

import com.aura.data.network.ManageClient
import com.aura.data.repository.BankRepository
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for the [DataModule] dependency injection provider methods.
 *
 * This class verifies that the `provideBankRepository` function correctly
 * returns an instance of [BankRepository] when given a [ManageClient].
 */
class DataModuleTest {

    /**
     * Tests that [DataModule.provideBankRepository] returns a non-null [BankRepository] instance
     * when provided with a [ManageClient] dependency.
     */
    @Test
    fun `test provideBankRepository returns BankRepository`() {
        // Arrange: Mock a ManageClient instance
        val manageClient = mockk<ManageClient>()

        // Act: Call the provider method manually
        val bankRepository = DataModule.provideBankRepository(manageClient)

        // Assert: Verify the returned object is not null and is of the expected type
        assertNotNull(bankRepository)
        assertTrue(bankRepository is BankRepository)
    }
}
