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
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import retrofit2.Response

class BankRepositoryTest {
    private lateinit var cut: BankRepository //Class Under Test
    private lateinit var dataService: ManageClient

    @Before
    fun setup() {
        dataService = mockk()
        cut = BankRepository(dataService)
    }


    @Test
    fun `assert when getLogin is requested then clean data is provided`() = runTest {
        //given
        val loginResponse = LoginBankResponse(
            granted = true
        )

        coEvery {
            dataService.fetchAccess(any())
        } returns Response.success(loginResponse)

        //when
        val result = run {
            cut.getLogin("identifier", "password")
        }
        //then
        assertEquals(Result.Success(LoginReportModel(granted = true)), result)
    }

    @Test
    fun `assert when getBalance is requested then clean data is provided`() = runTest {
        // Given
        val accountResponse = listOf(
            AccountBankResponse(
                id = "1234",
                main = true,
                balance = 100.0
            )
        )

        coEvery {
            dataService.fetchBalance(any())
        } returns Response.success(accountResponse)

        // When
        val result = cut.getBalance("1234")

        // Then
        assertEquals(Result.Success(BalanceReportModel(100.0)), result)
    }

    @Test
    fun `assert when getTransfer is requested then clean data is provided`() = runTest {
        //given
        val transferResponse = TransferBankResponse(
            done = true
        )

        coEvery {
            dataService.fetchTransfer(any())
        } returns Response.success(transferResponse)

        //when
        val result = run {
            cut.getTransfer("1234", "5678", amount = 100.0)
        }
        //then
        assertEquals(Result.Success(TransferReportModel(done = true)), result)
    }
}

