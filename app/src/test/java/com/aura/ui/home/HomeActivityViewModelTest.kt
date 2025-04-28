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

/**
 * Unit tests for [HomeActivityViewModel].
 *
 * These tests validate the ViewModel's behavior in various scenarios such as:
 * - Initialization from [SavedStateHandle]
 * - Handling success and failure when fetching balance
 * - Updating and resetting the UI state
 *
 * Coroutines are controlled using [StandardTestDispatcher] to allow deterministic testing.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeActivityViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var dataRepository: BankRepository
    private lateinit var cut: HomeActivityViewModel
    private lateinit var testCurrentId: String
    private val testDispatcher = StandardTestDispatcher()

    /**
     * Setup before each test.
     * Initializes the main dispatcher for coroutine control, mocks dependencies, and creates ViewModel instance.
     */
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        testCurrentId = "testCurrentId"
        dataRepository = mockk()
        savedStateHandle = SavedStateHandle(mapOf(ConstantsApp.CURRENT_ID to testCurrentId))
        cut = HomeActivityViewModel(dataRepository, savedStateHandle)
    }

    /**
     * Clean up after each test.
     * Resets the main dispatcher.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Tests that the ViewModel's [HomeActivityViewModel.currentId] is initialized correctly
     * from the [SavedStateHandle].
     */
    @Test
    fun `test currentId initialized correctly from SavedStateHandle`() {
        assertEquals(testCurrentId, cut.currentId)
    }

    /**
     * Tests that the initial [QueryUiState] is correctly set to its default values (all null).
     */
    @Test
    fun `test initial uiState is default`() = runTest {
        val uiState = cut.uiState.value
        assertNull(uiState.isBalanceReady)
        assertNull(uiState.balance)
        assertNull(uiState.isViewLoading)
        assertNull(uiState.errorMessage)
    }

    /**
     * Tests that the [HomeActivityViewModel.getAuraBalance] method first emits a loading state.
     */
    @Test
    fun `test getAuraBalance loading`() = runTest {
        coEvery { dataRepository.getBalance(testCurrentId) } returns Result.Success(
            BalanceReportModel(100.0)
        )

        // When
        cut.getAuraBalance()
        delay(500)

        // Then
        cut.uiState.test {
            val uiStateTest = awaitItem()
            assertEquals(null, uiStateTest.isBalanceReady)
            assertEquals(null, uiStateTest.balance)
            assertEquals(true, uiStateTest.isViewLoading)
            assertEquals(null, uiStateTest.errorMessage)
        }
    }

    /**
     * Tests that [HomeActivityViewModel.getAuraBalance] updates the UI state correctly on a successful balance fetch.
     */
    @Test
    fun `test getAuraBalance updates UI state on success`() = runTest {
        val balance = 100.0
        coEvery { dataRepository.getBalance(testCurrentId) } returns Result.Success(
            BalanceReportModel(balance)
        )

        // When
        cut.getAuraBalance()
        delay(1100)

        // Then
        cut.uiState.test {
            val uiStateTest = awaitItem()
            assertEquals(true, uiStateTest.isBalanceReady)
            assertEquals(balance, uiStateTest.balance)
            assertEquals(false, uiStateTest.isViewLoading)
            assertEquals(null, uiStateTest.errorMessage)
        }
    }

    /**
     * Tests that [HomeActivityViewModel.getAuraBalance] handles errors and updates the UI state correctly on failure.
     */
    @Test
    fun `test getAuraBalance failure updates errorMessage`() = runTest {
        val errorMessage = "Some Error Message"
        coEvery { dataRepository.getBalance(any()) } returns Result.Failure(errorMessage)

        // When
        cut.getAuraBalance()
        delay(1100)

        // Then
        cut.uiState.test {
            val uiStateTest = awaitItem()
            assertEquals(false, uiStateTest.isBalanceReady)
            assertEquals(null, uiStateTest.balance)
            assertEquals(false, uiStateTest.isViewLoading)
            assertEquals(errorMessage, uiStateTest.errorMessage)
        }
    }

    /**
     * Tests that the [HomeActivityViewModel.reset] function clears the current [QueryUiState] to default values.
     */
    @Test
    fun `test reset clears the UI state`() = runTest {
        cut._uiState.update {
            it.copy(
                balance = 1000.0,
                isBalanceReady = true,
                isViewLoading = true,
                errorMessage = "Some error"
            )
        }

        // When
        cut.reset()
        val uiState = cut.uiState.value

        // Then
        assertNull(uiState.balance)
        assertNull(uiState.isViewLoading)
        assertNull(uiState.errorMessage)
    }
}
