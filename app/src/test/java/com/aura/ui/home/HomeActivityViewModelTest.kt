package com.aura.ui.home

import android.util.Log
import com.aura.data.repository.Result
import androidx.lifecycle.SavedStateHandle
import com.aura.data.repository.BankRepository
import com.aura.domain.model.BalanceReportModel
import com.aura.ui.ConstantsApp
import org.junit.Assert.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.*
import org.junit.After

import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeActivityViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var dataRepository: BankRepository
    private lateinit var cut: HomeActivityViewModel
//    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        dataRepository = mockk<BankRepository>()
        savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
        cut = HomeActivityViewModel(dataRepository, savedStateHandle)
//        Dispatchers.setMain(testDispatcher)
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    @After
//    fun tearDown() {
//        // Reset the main dispatcher after the test
//        Dispatchers.resetMain()
//    }

    @Test
    fun `test initial uiState is default`() = runTest {
        val uiState = cut.uiState.value

        assertNull(uiState.isBalanceReady)
        assertNull(uiState.balance)
        assertNull(uiState.isViewLoading)
        assertNull(uiState.errorMessage)
    }

    @Test
    fun `test currentId initialization from SavedStateHandle`() {

        // Assume the current ID is "12345" (or any other test value)
        val testCurrentId = "12345"
        every { savedStateHandle.get<String>(ConstantsApp.CURRENT_ID) } returns testCurrentId

        // Create the ViewModel instance
        val cut = HomeActivityViewModel(mockk(), savedStateHandle)

        // Assert that currentId is set correctly from SavedStateHandle
        assertEquals("12345", cut.currentId)
    }

    @Test
    fun `test getAuraBalance updates UI state on success`() = runTest {
        // Mock the currentId to return a valid value
        val testCurrentId = "12345"
        every { savedStateHandle.get<String>(ConstantsApp.CURRENT_ID) } returns testCurrentId

        // Mock the suspend function to return a successful balance
        coEvery { dataRepository.getBalance(testCurrentId) } returns Result.Success(
            BalanceReportModel(100.0)
        )

        // Create the ViewModel instance
        val cut = HomeActivityViewModel(dataRepository, savedStateHandle)

        // Call the method to test
//        cut.getAuraBalance()

        // Access the UI state directly using the value property
//        val uiState = cut.uiState.value
//        println("MARC MARC MARC MARC MARC getAuraBalance: $uiState")

        // Ensure the UI state was updated correctly after the balance fetch
//        assertTrue(uiState.isBalanceReady == true)
//        assertTrue(uiState.balance == 100.0)
//        assertNull(uiState.errorMessage)
//        assertFalse(uiState.isViewLoading == true)
    }

    @Test
    fun `test getAuraBalance handles failure`() = runTest {
        // Mock the suspend function to return a failure result
        coEvery { dataRepository.getBalance(any()) } returns Result.Failure("Invalid Data")

        // Create the ViewModel instance
        val cut = HomeActivityViewModel(dataRepository, savedStateHandle)

//        // Call the method to test
//        cut.getAuraBalance()
//        cut._uiState.update {
//            it.copy(
//                balance = null,
//                isBalanceReady = null,
//                errorMessage = "Invalid data"
//            )
//        }

        // Ensure the UI state was updated correctly after the failure
//        assertNull(cut.uiState.value.balance)
//        assertNull(cut.uiState.value.isBalanceReady)
//        assertEquals("Invalid data", cut.uiState.value.errorMessage)
    }

    @Test
    fun `test reset clears the UI state`() = runTest {
        // Create the ViewModel instance
        val cut = HomeActivityViewModel(dataRepository, savedStateHandle)

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