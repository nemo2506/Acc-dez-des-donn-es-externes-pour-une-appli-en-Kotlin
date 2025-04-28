package com.aura.ui.home

import com.aura.data.repository.Result
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.aura.data.repository.BankRepository
import com.aura.domain.model.BalanceReportModel
import com.aura.ui.ConstantsApp
import org.junit.Assert.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.*
import org.junit.After

import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeActivityViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var dataRepository: BankRepository
    private lateinit var cut: HomeActivityViewModel
    private lateinit var testCurrentId: String
    private val testDispatcher = StandardTestDispatcher() // To control coroutine execution

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher) // Set the main dispatcher for testing
        testCurrentId = "testCurrentId"
        dataRepository = mockk<BankRepository>()
        savedStateHandle = SavedStateHandle(mapOf(ConstantsApp.CURRENT_ID to testCurrentId))
        cut = HomeActivityViewModel(dataRepository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the dispatcher after tests
    }

    @Test
    fun `test currentId initialized correctly from SavedStateHandle`() {
        assertEquals(testCurrentId, cut.currentId)
    }

    @Test
    fun `test initial uiState is default`() = runTest {
        val uiState = cut.uiState.value

        assertNull(uiState.isBalanceReady)
        assertNull(uiState.balance)
        assertNull(uiState.isViewLoading)
        assertNull(uiState.errorMessage)
    }

    @Test
    fun `test getAuraBalance loading`() = runTest {
        // Mock the currentId to return a valid value

        // Mock the suspend function to return a successful balance
        coEvery { dataRepository.getBalance(testCurrentId) } returns Result.Success(
            BalanceReportModel(100.0)
        )

        // When
        cut.getAuraBalance()

        // Then
        delay(500)
        cut.uiState.test {
            val expectedState = QueryUiState( isBalanceReady = null, balance = null, isViewLoading = true, errorMessage = null )
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `test getAuraBalance updates UI state on success`() = runTest {
        // Mock the balance to return a valid value
        val balance = 100.0

        // Mock the suspend function to return a successful balance
        coEvery { dataRepository.getBalance(testCurrentId) } returns Result.Success(
            BalanceReportModel(balance = balance)
        )

        // When
        cut.getAuraBalance()

        // Then
        delay(1100)
        cut.uiState.test {
            val expectedState = QueryUiState(isBalanceReady = true, balance = balance, isViewLoading=false, errorMessage=null)
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `test getAuraBalance handles failure`() = runTest {
        val errorMessage = "Some Error Message"
        // Mock the suspend function to return a failure result
        coEvery { dataRepository.getBalance(any()) } returns Result.Failure(errorMessage)

        // When
        cut.getAuraBalance()

        // Then
        delay(1100)
        cut.uiState.test {
            val expectedState = QueryUiState(isBalanceReady = false, balance = null, isViewLoading=false, errorMessage=errorMessage)
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `test reset clears the UI state`() = runTest {

        // Set some initial values in the UI state
        cut._uiState.update {
            it.copy(
                balance = 1000.0,
                isBalanceReady = true,
                errorMessage = "Some error"
            )
        }

        // Call the reset() method
        cut.reset()

        // Access the UI state directly using the value property
        val uiState = cut.uiState.value

        // Assert that the UI state has been reset to null values
        assertNull(uiState.balance)
        assertNull(uiState.isBalanceReady)
        assertNull(uiState.errorMessage)
    }
}