package com.aura.data.response

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [AccountBankResponse] including domain model conversion
 * and JSON serialization/deserialization.
 */
class AccountBankResponseTest {

    private lateinit var moshi: Moshi

    /**
     * Set up the Moshi instance with the Kotlin adapter before each test.
     */
    @Before
    fun setUp() {
        // Initialize Moshi with KotlinJsonAdapterFactory for Kotlin classes
        moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    /**
     * Test that [AccountBankResponse] correctly converts to the domain model.
     */
    @Test
    fun `test AccountBankResponse to Account domain model conversion`() {
        // Given: A mock AccountBankResponse object
        val accountResponse = AccountBankResponse(id = "123", main = true, balance = 1500.0)

        // When: Converting to domain model
        val domainModel = accountResponse.toDomainModel()

        // Then: Verify all fields are mapped correctly
        assertNotNull(domainModel)
        assertEquals(accountResponse.id, domainModel.id)
        assertEquals(accountResponse.main, domainModel.main)
        assertEquals(accountResponse.balance, domainModel.balance, 0.0)
    }

    /**
     * Test JSON serialization and deserialization for [AccountBankResponse]
     * using Moshi to ensure the object can be accurately converted to and from JSON.
     */
    @Test
    fun `test AccountBankResponse serialization and deserialization`() {
        // Given: A mock AccountBankResponse object
        val accountResponse = AccountBankResponse(id = "123", main = true, balance = 1500.0)

        // Serialize the object to JSON
        val json = moshi.adapter(AccountBankResponse::class.java).toJson(accountResponse)

        // When: Deserializing the JSON back into an object
        val deserializedResponse = moshi.adapter(AccountBankResponse::class.java).fromJson(json)

        // Then: Verify the deserialized object matches the original
        assertNotNull(deserializedResponse)
        assertEquals(accountResponse.id, deserializedResponse?.id)
        assertEquals(accountResponse.main, deserializedResponse?.main)
        deserializedResponse?.balance?.let { assertEquals(accountResponse.balance, it, 0.0) }
    }
}
