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
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

class ManageClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var manageClient: ManageClient

    @Before
    fun setUp() {
        // Initialize MockWebServer
        mockWebServer = MockWebServer()

        // Set up Retrofit with the MockWebServer URL
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))  // Use the MockWebServer URL
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        // Create ManageClient instance
        manageClient = retrofit.create(ManageClient::class.java)
    }

    @After
    fun tearDown() {
        // Shut down MockWebServer after test
        mockWebServer.shutdown()
    }

    @Test
    fun `test fetchAccess returns LoginBankResponse on success`() = runBlocking {
        // Arrange: Set up MockWebServer to return a success response
        val loginResponse = """
            {
                "granted": true
            }
        """
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(loginResponse))

        // Act: Make API call using the mock
        val user = User(id = "12345", password = "password")
        val response: Response<LoginBankResponse> = manageClient.fetchAccess(user)
        val logged = response.body()?.granted

        // Assert: Check if the response is successful and the token is returned
        assert(response.isSuccessful)
        assert(logged == true)
    }

    @Test
    fun `test fetchAccess returns LoginBankResponse on failed`() = runBlocking {
        // Arrange: Set up MockWebServer to return a success response
        val loginResponse = """
            {
                "granted": false
            }
        """
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(loginResponse))

        // Act: Make API call using the mock
        val user = User(id = "12345", password = "password")
        val response: Response<LoginBankResponse> = manageClient.fetchAccess(user)
        val logged = response.body()?.granted

        // Assert: Check if the response is successful and the token is returned
        assert(response.isSuccessful)
        assertFalse(logged == true)
    }

    @Test
    fun `test fetchBalance returns AccountBankResponse on success`() = runBlocking {
        // Arrange: Set up MockWebServer to return a success response
        val balanceResponse = """
            [
                {
                    "id": "1",
                    "main": true,
                    "balance": 1000.0
                }
            ]
        """
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(balanceResponse))

        // Act: Make API call using the mock
        val response: Response<List<AccountBankResponse>> = manageClient.fetchBalance("12345")
        val account = response.body()?.first()

        // Assert: Check if the response is successful and contains the correct data
        assert(response.isSuccessful)
        assert(account?.balance == 1000.0)
    }

    @Test
    fun `test fetchBalance returns empty list when no accounts found`() = runBlocking {
        // Arrange: Set up MockWebServer to return a success response with an empty list
        val balanceResponse = """
        []
    """
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(balanceResponse))

        // Act: Make API call using the mock
        val response: Response<List<AccountBankResponse>> = manageClient.fetchBalance("12345")

        // Assert: Check if the response is successful and contains an empty list
        val accountList = response.body()
        // Additional assert: if we want to make sure no accounts are present
        val account = accountList?.firstOrNull()

        assert(response.isSuccessful)
        assertNotNull(accountList)
        if (accountList != null) {
            assertTrue(accountList.isEmpty())
        }
        assertNull(account)  // Should be null since the list is empty
    }

    @Test
    fun `test fetchTransfer returns TransferBankResponse on success`() = runBlocking {
        // Arrange: Set up MockWebServer to return a success response
        val transferResponse = """
            {
                "result": true
            }
        """
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(transferResponse))

        // Act: Make API call using the mock
        val transfer = Transfer(sender = "12345", recipient = "56789", amount = 100.0)
        val response: Response<TransferBankResponse> = manageClient.fetchTransfer(transfer)
        val result = response.body()?.done

        // Assert: Check if the response is successful and the status is correct
        assert(response.isSuccessful)
        assert(result == true)
    }

    @Test
    fun `test fetchTransfer returns TransferBankResponse on failed`() = runBlocking {
        // Arrange: Set up MockWebServer to return a success response
        val transferResponse = """
            {
                "result": false
            }
        """
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(transferResponse))

        // Act: Make API call using the mock
        val transfer = Transfer(sender = "12345", recipient = "56789", amount = 100.0)
        val response: Response<TransferBankResponse> = manageClient.fetchTransfer(transfer)
        val result = response.body()?.done

        // Assert: Check if the response is successful and the status is correct
        assert(response.isSuccessful)
        assert(result == false)
    }

    @Test
    fun `test fetchTransfer returns failure on 500 internal server error`() = runBlocking {
        // Arrange: Set up MockWebServer to return a 500 Internal Server Error response
        val errorResponse = """
        {
            "error": "Internal Server Error"
        }
    """
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody(errorResponse))

        // Act: Make API call using the mock
        val transfer = Transfer(sender = "12345", recipient = "56789", amount = 100.0)
        val response: Response<TransferBankResponse> = manageClient.fetchTransfer(transfer)
        // Optionally: You can check if the error body contains the expected error message
        val errorBody = response.errorBody()?.string()

        // Assert: Check if the response is not successful due to the 500 error
        assert(!response.isSuccessful)
        assertTrue(errorBody?.contains("Internal Server Error") == true)
    }
}
