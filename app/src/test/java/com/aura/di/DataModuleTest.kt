package com.aura.di

import com.aura.data.network.ManageClient
import com.aura.data.repository.BankRepository
import io.mockk.mockk
import org.junit.Assert.*

import org.junit.Test

class DataModuleTest {

    @Test
    fun `test provideBankRepository returns BankRepository`() {
        // Arrange: Mock ManageClient
        val manageClient = mockk<ManageClient>()

        // Act: Call the provide function manually
        val bankRepository = DataModule.provideBankRepository(manageClient)

        // Assert: Check if it's not null and correct type
        assertNotNull(bankRepository)
        assertTrue(bankRepository is BankRepository)
    }
}