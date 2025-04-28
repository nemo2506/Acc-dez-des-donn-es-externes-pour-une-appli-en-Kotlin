package com.aura.data.network

import com.aura.data.response.AccountBankResponse
import com.aura.data.response.LoginBankResponse
import com.aura.data.response.TransferBankResponse
import com.aura.domain.model.Transfer
import com.aura.domain.model.User
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for [ManageClient] network calls.
 *
 * Tests are using [MockWebServer] to mock API responses and verify
 * the behavior of network calls made through Retrofit.
 */
class ManageClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var manageClient: ManageClient

    /**
     * Sets up the test environment before each test:
     * - Initializes a [MockWebServer].
     * - Configures a [Retrofit] instance pointing to the MockWebServer.
     */
    @Before
    fun setUp() {
        mockWebServer = MockWebServer()

        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        manageClient = retrofit.create(ManageClient::class.java)
    }

    /**
     * Shuts down the [MockWebServer] after each test.
     */
    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    /**
     * Verifies that [fetchAccess] returns a successful [LoginBankResponse]
     * when the API responds with a granted=true response.
     */
    @Test
    fun `test fetchAccess returns LoginBankResponse on success`() = runBlocking {
        val loginResponse = """ { "granted": true } """
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(loginResponse))

        val user = User(id = "12345", password = "password")
        val response = manageClient.fetchAccess(user)
        val logged = response.body()?.granted

        assert(response.isSuccessful)
        assert(logged == true)
    }

    /**
     * Verifies that [fetchAccess] returns a successful [LoginBankResponse]
     * with granted=false when the API denies access.
     */
    @Test
    fun `test fetchAccess returns LoginBankResponse on failed`() = runBlocking {
        val loginResponse = """ { "granted": false } """
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(loginResponse))

        val user = User(id = "12345", password = "password")
        val response = manageClient.fetchAccess(user)
        val logged = response.body()?.granted

        assert(response.isSuccessful)
        assertFalse(logged == true)
    }

    /**
     * Verifies that [fetchAccess] handles a 500 Internal Server Error properly.
     */
    @Test
    fun `test fetchAccess returns failure on 500 internal server error`() = runBlocking {
        val errorResponse = """ { "error": "Internal Server Error" } """
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody(errorResponse))

        val user = User(id = "12345", password = "password")
        val response = manageClient.fetchAccess(user)
        val errorBody = response.errorBody()?.string()

        assert(!response.isSuccessful)
        assertTrue(errorBody?.contains("Internal Server Error") == true)
    }

    /**
     * Verifies that [fetchBalance] returns a successful [AccountBankResponse]
     * when the API returns valid account data.
     */
    @Test
    fun `test fetchBalance returns AccountBankResponse on success`() = runBlocking {
        val balanceResponse = """ [ { "id": "1", "main": true, "balance": 1000.0 } ] """
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(balanceResponse))

        val response = manageClient.fetchBalance("12345")
        val account = response.body()?.first()

        assert(response.isSuccessful)
        assert(account?.balance == 1000.0)
    }

    /**
     * Verifies that [fetchBalance] returns an empty list when no accounts are found.
     */
    @Test
    fun `test fetchBalance returns AccountBankResponse when no accounts found`() = runBlocking {
        val balanceResponse = """ [] """
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(balanceResponse))

        val response = manageClient.fetchBalance("12345")
        val accountList = response.body()
        val account = accountList?.firstOrNull()

        assert(response.isSuccessful)
        assertNotNull(accountList)
        if (accountList != null) assertTrue(accountList.isEmpty())
        assertNull(account)
    }

    /**
     * Verifies that [fetchBalance] handles a 500 Internal Server Error properly.
     */
    @Test
    fun `test fetchBalance returns failure on 500 internal server error`() = runBlocking {
        val errorResponse = """ { "error": "Internal Server Error" } """
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody(errorResponse))

        val response = manageClient.fetchBalance("12345")
        val errorBody = response.errorBody()?.string()

        assert(!response.isSuccessful)
        assertTrue(errorBody?.contains("Internal Server Error") == true)
    }

    /**
     * Verifies that [fetchTransfer] returns a successful [TransferBankResponse]
     * when the transfer is completed successfully.
     */
    @Test
    fun `test fetchTransfer returns TransferBankResponse on success`() = runBlocking {
        val transferResponse = """ { "result": true } """
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(transferResponse))

        val transfer = Transfer(sender = "12345", recipient = "56789", amount = 100.0)
        val response = manageClient.fetchTransfer(transfer)
        val result = response.body()?.done

        assert(response.isSuccessful)
        assert(result == true)
    }

    /**
     * Verifies that [fetchTransfer] returns a [TransferBankResponse]
     * indicating failure when the transfer is not completed.
     */
    @Test
    fun `test fetchTransfer returns TransferBankResponse on failed`() = runBlocking {
        val transferResponse = """ { "result": false } """
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(transferResponse))

        val transfer = Transfer(sender = "12345", recipient = "56789", amount = 100.0)
        val response = manageClient.fetchTransfer(transfer)
        val result = response.body()?.done

        assert(response.isSuccessful)
        assert(result == false)
    }

    /**
     * Verifies that [fetchTransfer] handles a 500 Internal Server Error properly.
     */
    @Test
    fun `test fetchTransfer returns failure on 500 internal server error`() = runBlocking {
        val errorResponse = """ { "error": "Internal Server Error" } """
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody(errorResponse))

        val transfer = Transfer(sender = "12345", recipient = "56789", amount = 100.0)
        val response = manageClient.fetchTransfer(transfer)
        val errorBody = response.errorBody()?.string()

        assert(!response.isSuccessful)
        assertTrue(errorBody?.contains("Internal Server Error") == true)
    }
}
