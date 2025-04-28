package com.aura.data.response

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [TransferBankResponse], including domain model conversion
 * and JSON serialization/deserialization using Moshi.
 */
class TransferBankResponseTest {

    private lateinit var moshi: Moshi

    /**
     * Sets up the Moshi instance with the Kotlin JSON adapter before each test.
     */
    @Before
    fun setUp() {
        // Initialize Moshi with KotlinJsonAdapterFactory to support Kotlin data classes
        moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    /**
     * Tests that a [TransferBankResponse] correctly converts to a [TransferReportModel] domain model.
     */
    @Test
    fun `test TransferBankResponse to TransferReportModel conversion`() {
        // Given: A mock TransferBankResponse object
        val transferResponse = TransferBankResponse(done = true)

        // When: Converting to domain model
        val domainModel = transferResponse.toDomainModel()

        // Then: Verify the domain model is not null and reflects the correct 'done' status
        assertNotNull(domainModel)
        domainModel.done?.let { assertTrue(it) }  // 'done' should be true
    }

    /**
     * Tests JSON serialization and deserialization of [TransferBankResponse]
     * using Moshi to ensure data consistency.
     */
    @Test
    fun `test TransferBankResponse serialization and deserialization`() {
        // Given: A mock TransferBankResponse object
        val transferResponse = TransferBankResponse(done = true)

        // Serialize the object into JSON
        val json = moshi.adapter(TransferBankResponse::class.java).toJson(transferResponse)

        // When: Deserializing the JSON back into an object
        val deserializedResponse = moshi.adapter(TransferBankResponse::class.java).fromJson(json)

        // Then: Verify that the deserialized object matches the original data
        assertNotNull(deserializedResponse)
        assertTrue(deserializedResponse?.done == true)
    }
}
