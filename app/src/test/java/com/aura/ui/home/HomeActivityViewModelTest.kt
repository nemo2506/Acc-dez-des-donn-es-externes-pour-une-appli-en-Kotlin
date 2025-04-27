package com.aura.ui.home

import com.aura.data.repository.Result
import androidx.lifecycle.SavedStateHandle
import com.aura.data.repository.BankRepository
import com.aura.domain.model.BalanceReportModel
import com.aura.ui.ConstantsApp
import org.junit.Assert.*
import io.mockk.*
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.*

import org.junit.Before
import org.junit.Test

class HomeActivityViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var dataRepository: BankRepository
    private lateinit var cut: HomeActivityViewModel
    private lateinit var testCurrentId: String

    @Before
    fun setup() {
        testCurrentId = "testCurrentId"
        dataRepository = mockk<BankRepository>()
        savedStateHandle = SavedStateHandle(mapOf(ConstantsApp.CURRENT_ID to testCurrentId))
        cut = HomeActivityViewModel(dataRepository, savedStateHandle)
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

//    @Test
//    fun `test getAuraBalance updates UI state on success`() = runTest {
//        // Mock the currentId to return a valid value
//
//        // Mock the suspend function to return a successful balance
//        coEvery { dataRepository.getBalance(testCurrentId) } returns Result.Success(
//            BalanceReportModel(100.0)
//        )
//
//        // Call the method to test
//        cut.getAuraBalance()
//
//        // Access the UI state directly using the value property
//        val uiState = cut.uiState.value
//
//        // Ensure the UI state was updated correctly after the balance fetch
//        assertTrue(uiState.isBalanceReady == true)
//        assertTrue(uiState.balance == 100.0)
//        assertNull(uiState.errorMessage)
//        assertFalse(uiState.isViewLoading == true)
//    }

//    @Test
//    fun `test getAuraBalance handles failure`() = runTest {
//        val errorMessage = "Some Error Message"
//        // Mock the suspend function to return a failure result
//        coEvery { dataRepository.getBalance(any()) } returns Result.Failure(errorMessage)
//
//        // Call the method to test
//        cut.getAuraBalance()
//
//        // Ensure the UI state was updated correctly after the failure
//        assertNull(cut.uiState.value.balance)
//        assertNull(cut.uiState.value.isBalanceReady)
//        assertEquals(errorMessage, cut.uiState.value.errorMessage)
//    }

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