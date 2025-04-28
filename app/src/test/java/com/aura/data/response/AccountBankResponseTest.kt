package com.aura.data.response

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class AccountBankResponseTest {
    private lateinit var moshi: Moshi

    @Before
    fun setUp() {
        // Initialize Moshi with Kotlin adapter
        moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    // Test AccountBankResponse to Account domain model conversion
    @Test
    fun `test AccountBankResponse to Account domain model conversion`() {
        val accountResponse = AccountBankResponse(id = "123", main = true, balance = 1500.0)

        val domainModel = accountResponse.toDomainModel()

        assertNotNull(domainModel)
        assertEquals(accountResponse.id, domainModel.id)
        assertEquals(accountResponse.main, domainModel.main)
        assert(accountResponse.balance == domainModel.balance)
    }

    // Test JSON serialization and deserialization for AccountBankResponse
    @Test
    fun `test AccountBankResponse serialization and deserialization`() {
        val accountResponse = AccountBankResponse(id = "123", main = true, balance = 1500.0)

        // Serialize to JSON
        val json = moshi.adapter(AccountBankResponse::class.java).toJson(accountResponse)

        // Deserialize from JSON
        val deserializedResponse = moshi.adapter(AccountBankResponse::class.java).fromJson(json)

        assertNotNull(deserializedResponse)
        assertEquals(accountResponse.id, deserializedResponse?.id)
        assertEquals(accountResponse.main, deserializedResponse?.main)
        assertEquals(accountResponse.balance, deserializedResponse?.balance)
    }
}