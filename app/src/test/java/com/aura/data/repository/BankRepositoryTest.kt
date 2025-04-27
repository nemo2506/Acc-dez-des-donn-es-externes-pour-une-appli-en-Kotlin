package com.aura.data.repository

import com.aura.data.network.ManageClient
import com.aura.data.response.AccountBankResponse
import com.aura.data.response.LoginBankResponse
import com.aura.data.response.TransferBankResponse
import com.aura.domain.model.BalanceReportModel
import com.aura.domain.model.LoginReportModel
import com.aura.domain.model.TransferReportModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Unit tests for the [BankRepository] class.
 *
 * These tests validate the correct transformation of network responses
 * into domain-level models and proper error handling.
 */
class BankRepositoryTest {

    private lateinit var cut: BankRepository // Class Under Test
    private lateinit var dataService: ManageClient

    /**
     * Sets up the test environment before each test runs.
     * Mocks the [ManageClient] dependency and injects it into the repository.
     */
    @Before
    fun setup() {
        dataService = mockk()
        cut = BankRepository(dataService)
    }

    /**
     * Verifies that [BankRepository.getLogin] returns a success result
     * when the backend response indicates access is granted
     */
    @Test
    fun `assert when getLogin is requested then clean data is provided when result is true`() = runTest {
        // Success case
        val loginResponse = LoginBankResponse(granted = true)
        coEvery { dataService.fetchAccess(any()) } returns Response.success(loginResponse)

        val resultSuccess = cut.getLogin("identifier", "password")
        assertEquals(Result.Success(LoginReportModel(granted = true)), resultSuccess)
    }

    /**
     * Verifies that [BankRepository.getLogin] returns a false result
     * when the backend response indicates access is no-granted, and returns a failure
     * when the backend responds with an error.
     */
    @Test
    fun `assert when getLogin is requested then clean data is provided when result is false`() = runTest {
        // Error case
        coEvery {
            dataService.fetchAccess(any())
        } returns Response.error(
            400,
            "Bad Request".toResponseBody("application/json".toMediaTypeOrNull())
        )

        val resultFailure = cut.getLogin("identifier", "password")
        assertEquals(Result.Failure("Invalid data"), resultFailure)
    }

    /**
     * Verifies that [BankRepository.getBalance] returns the main account's balance
     * on success, and returns a failure when the backend responds with an error.
     */
    @Test
    fun `assert when getBalance is requested then clean data is provided when result is true`() = runTest {
        // Success case
        val accountResponse = listOf(
            AccountBankResponse(id = "identifiant", main = true, balance = 100.0)
        )

        coEvery { dataService.fetchBalance(any()) } returns Response.success(accountResponse)

        val resultSuccess = cut.getBalance("identifiant")
        assertEquals(Result.Success(BalanceReportModel(100.0)), resultSuccess)
    }

    /**
     * Verifies that [BankRepository.getBalance] returns the main account's balance
     * on success, and returns a failure when the backend responds with an error.
     */
    @Test
    fun `assert when getBalance is requested then clean data is provided when result is false`() = runTest {
        // Error case
        coEvery {
            dataService.fetchBalance(any())
        } returns Response.error(
            400,
            "Bad Request".toResponseBody("application/json".toMediaTypeOrNull())
        )

        val resultFailure = cut.getBalance("identifiant")
        assertEquals(Result.Failure("Invalid data"), resultFailure)
    }

    /**
     * Verifies that [BankRepository.getTransfer] returns a success result
     * when the transfer is completed, and returns a failure when the transfer fails.
     */
    @Test
    fun `assert when getTransfer is requested then clean data is provided when result is true`() = runTest {
        // Success case
        val transferResponse = TransferBankResponse(done = true)

        coEvery { dataService.fetchTransfer(any()) } returns Response.success(transferResponse)

        val resultSuccess = cut.getTransfer("idendifiant1", "idendifiant2", amount = 100.0)
        assertEquals(Result.Success(TransferReportModel(done = true)), resultSuccess)
    }

    /**
     * Verifies that [BankRepository.getTransfer] returns a success result
     * when the transfer is completed, and returns a failure when the transfer fails.
     */
    @Test
    fun `assert when getTransfer is requested then clean data is provided when result is false`() = runTest {
        // Error case
        coEvery {
            dataService.fetchTransfer(any())
        } returns Response.error(
            400,
            "Bad Request".toResponseBody("application/json".toMediaTypeOrNull())
        )

        val resultFailure = cut.getTransfer("1234", "5678", amount = 100.0)
        assertEquals(Result.Failure("Invalid data"), resultFailure)
    }
}
