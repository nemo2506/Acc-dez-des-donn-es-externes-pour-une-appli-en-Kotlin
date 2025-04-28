package com.aura.data.response

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class LoginBankResponseTest {

    private lateinit var moshi: Moshi

    @Before
    fun setUp() {
        // Initialize Moshi with Kotlin adapter
        moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    // Test LoginBankResponse to LoginReportModel conversion
    @Test
    fun `test LoginBankResponse to LoginReportModel conversion`(){
        val loginResponse = LoginBankResponse(granted = true)

        val domainModel = loginResponse.toDomainModel()

        assertNotNull(domainModel)
        domainModel.granted?.let { assertTrue(it) }  // Since 'granted' is true in the response
    }


    // Test JSON serialization and deserialization for LoginBankResponse
    @Test
    fun `test LoginBankResponse serialization and deserialization`() {
        val loginResponse = LoginBankResponse(granted = true)

        // Serialize to JSON
        val json = moshi.adapter(LoginBankResponse::class.java).toJson(loginResponse)

        // Deserialize from JSON
        val deserializedResponse = moshi.adapter(LoginBankResponse::class.java).fromJson(json)

        assertNotNull(deserializedResponse)
        assertTrue(deserializedResponse?.granted == true)
    }
}