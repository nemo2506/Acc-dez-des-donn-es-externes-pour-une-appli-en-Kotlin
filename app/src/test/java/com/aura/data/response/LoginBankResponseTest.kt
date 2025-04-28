package com.aura.data.response

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [LoginBankResponse], including testing domain model conversion
 * and JSON serialization/deserialization using Moshi.
 */
class LoginBankResponseTest {

    private lateinit var moshi: Moshi

    /**
     * Set up the Moshi instance with Kotlin adapter before each test.
     */
    @Before
    fun setUp() {
        // Initialize Moshi with KotlinJsonAdapterFactory for Kotlin classes
        moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    /**
     * Test that [LoginBankResponse] correctly converts to the [LoginReportModel] domain model.
     */
    @Test
    fun `test LoginBankResponse to LoginReportModel conversion`() {
        // Given: A mock LoginBankResponse object
        val loginResponse = LoginBankResponse(granted = true)

        // When: Converting to domain model
        val domainModel = loginResponse.toDomainModel()

        // Then: Verify the domain model is not null and contains correct values
        assertNotNull(domainModel)
        domainModel.granted?.let { assertTrue(it) }  // 'granted' should be true
    }

    /**
     * Test JSON serialization and deserialization for [LoginBankResponse]
     * using Moshi to ensure accurate data conversion to and from JSON.
     */
    @Test
    fun `test LoginBankResponse serialization and deserialization`() {
        // Given: A mock LoginBankResponse object
        val loginResponse = LoginBankResponse(granted = true)

        // Serialize the object to JSON
        val json = moshi.adapter(LoginBankResponse::class.java).toJson(loginResponse)

        // When: Deserializing the JSON back into an object
        val deserializedResponse = moshi.adapter(LoginBankResponse::class.java).fromJson(json)

        // Then: Verify the deserialized object matches the original
        assertNotNull(deserializedResponse)
        assertTrue(deserializedResponse?.granted == true)
    }
}
