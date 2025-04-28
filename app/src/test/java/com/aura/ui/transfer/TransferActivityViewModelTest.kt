package com.aura.ui.transfer

import com.aura.ui.transfer.QueryUiState
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.aura.data.repository.BankRepository
import com.aura.data.repository.Result
import com.aura.domain.model.TransferReportModel
import com.aura.ui.ConstantsApp
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransferActivityViewModelTest {

    private lateinit var dataRepository: BankRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var cut: TransferActivityViewModel
    private val testDispatcher = StandardTestDispatcher() // To control coroutine execution

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set the main dispatcher for testing
        dataRepository = mockk()
        savedStateHandle = SavedStateHandle(mapOf(ConstantsApp.CURRENT_ID to "testCurrentId"))
        cut = TransferActivityViewModel(dataRepository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the dispatcher after tests
    }

    @Test
    fun `test currentId initialized correctly from SavedStateHandle`() {
        assertEquals("testCurrentId", cut.currentId)
    }

    @Test
    fun `test initial uiState is default`() {
        val uiState = cut.uiState.value

        assertNull(uiState.isUserDataReady)
        assertNull(uiState.transferred)
        assertNull(uiState.isViewLoading)
        assertNull(uiState.errorMessage)
    }


    @Test
    fun `test userDataControl with empty recipient and empty amount`() = runTest {
        val recipient = ""
        val amount = ""
        cut.userDataControl(recipient.isNotEmpty(), amount.isNotEmpty())
        val uiState = cut.uiState.value
        uiState.isUserDataReady?.let { assertFalse(it) }
    }

    @Test
    fun `test userDataControl with existed recipient and empty amount`() = runTest {
        val recipient = "r"
        val amount = ""
        cut.userDataControl(recipient.isNotEmpty(), amount.isNotEmpty())
        val uiState = cut.uiState.value
        uiState.isUserDataReady?.let { assertFalse(it) }
    }

    @Test
    fun `test userDataControl with empty recipient and existed amount`() = runTest {
        val recipient = ""
        val amount = "0.0"
        cut.userDataControl(recipient.isNotEmpty(), amount.isNotEmpty())
        val uiState = cut.uiState.value
        uiState.isUserDataReady?.let { assertFalse(it) }
    }

    @Test
    fun `test userDataControl with existed recipient and existed amount`() = runTest {
        val recipient = "r"
        val amount = "0.0"
        cut.userDataControl(recipient.isNotEmpty(), amount.isNotEmpty())
        val uiState = cut.uiState.value
        uiState.isUserDataReady?.let { assertTrue(it) }
    }

    @Test
    fun `test getAuraTransfer loading`() = runTest {
        // Arrange
        val recipient = "56789"
        val amount = 50.0
        coEvery { dataRepository.getTransfer("testCurrentId", recipient, amount) } returns
                Result.Success(TransferReportModel(done = true))

        // When
        cut.getAuraTransfer(recipient, amount)

        // Then
        delay(500)
        cut.uiState.test {
            val expectedState = QueryUiState( isUserDataReady = false, transferred = null, isViewLoading = true, errorMessage = null )
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `test getAuraTransfer success updates transferred true`() = runTest {
        // Arrange
        val recipient = "56789"
        val amount = 50.0
        coEvery { dataRepository.getTransfer("testCurrentId", recipient, amount) } returns
                Result.Success(TransferReportModel(done = true))

        // When
        cut.getAuraTransfer(recipient, amount)

        // Then
        delay(1100)
        cut.uiState.test {
            val expectedState = QueryUiState(isUserDataReady = false, transferred = true, isViewLoading = false, errorMessage = null)
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `test getAuraTransfer failure updates errorMessage`() = runTest {
        // Arrange
        val recipient = "56789"
        val amount = 50.0
        val errorMessage = "Transfer failed"
        coEvery { dataRepository.getTransfer("testCurrentId", recipient, amount) } returns
                Result.Failure(errorMessage)

        // When
        cut.getAuraTransfer(recipient, amount)


        // Then
        delay(1100)
        cut.uiState.test {
            val expectedState = QueryUiState(isUserDataReady = false, transferred = false, isViewLoading = false, errorMessage = errorMessage)
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `test reset clears the UI state`() = runTest {
        // Set some initial values in the UI state
        cut._uiState.update {
            it.copy(
                transferred = true,
                errorMessage = "Some Error"
            )
        }

        // Call the reset() method
        cut.reset()

        // Access the UI state directly using the value property
        val uiState = cut.uiState.value

        // Assert that the UI state has been reset to null values
        assertNull(uiState.transferred)
        assertNull(uiState.errorMessage)
    }
}