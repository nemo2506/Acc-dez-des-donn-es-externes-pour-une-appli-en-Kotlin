package com.aura.data.response

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class TransferBankResponseTest {
    private lateinit var moshi: Moshi

    @Before
    fun setUp() {
        // Initialize Moshi with Kotlin adapter
        moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    // Test TransferBankResponse to TransferReportModel conversion
    @Test
    fun `test TransferBankResponse to TransferReportModel conversion`() {
        val transferResponse = TransferBankResponse(done = true)

        val domainModel = transferResponse.toDomainModel()

        assertNotNull(domainModel)
        domainModel.done?.let { assertTrue(it) }  // Since 'done' is true in the response
    }

    // Test JSON serialization and deserialization for TransferBankResponse
    @Test
    fun `test TransferBankResponse serialization and deserialization`() {
        val transferResponse = TransferBankResponse(done = true)

        // Serialize to JSON
        val json = moshi.adapter(TransferBankResponse::class.java).toJson(transferResponse)

        // Deserialize from JSON
        val deserializedResponse = moshi.adapter(TransferBankResponse::class.java).fromJson(json)

        assertNotNull(deserializedResponse)
        assertTrue(deserializedResponse?.done == true)
    }

}