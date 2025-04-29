package com.aura.ui.transfer

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

/**
 * Unit tests for the [TransferActivityViewModel] class, which is responsible for handling
 * transfer-related actions and updating the UI state accordingly. This includes testing the
 * various methods and scenarios for initiating a transfer and managing UI state updates.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TransferActivityViewModelTest {

    private lateinit var dataRepository: BankRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var cut: TransferActivityViewModel
    private val testDispatcher = StandardTestDispatcher() // To control coroutine execution

    /**
     * Sets up the necessary components for testing, such as the repository, SavedStateHandle,
     * and the ViewModel instance. This method is called before each test is executed.
     */
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set the main dispatcher for testing
        dataRepository = mockk()
        savedStateHandle = SavedStateHandle(mapOf(ConstantsApp.CURRENT_ID to "testCurrentId"))
        cut = TransferActivityViewModel(dataRepository, savedStateHandle)
    }

    /**
     * Resets the dispatcher after tests to ensure that no state is carried over between tests.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the dispatcher after tests
    }

    /**
     * Test to ensure that the [currentId] is correctly initialized from the [SavedStateHandle].
     */
    @Test
    fun `test currentId initialized correctly from SavedStateHandle`() {
        assertEquals("testCurrentId", cut.currentId)
    }

    /**
     * Test to verify that the initial UI state in the ViewModel is set to default values.
     */
    @Test
    fun `test initial uiState is default`() {
        val uiState = cut.uiState.value

        assertNull(uiState.isUserDataReady)
        assertNull(uiState.transferred)
        assertNull(uiState.isViewLoading)
        assertNull(uiState.errorMessage)
    }

    /**
     * Test the behavior of [userDataControl] when both recipient and amount are empty.
     * It ensures that the UI state [isUserDataReady] is set to false.
     */
    @Test
    fun `test userDataControl with empty recipient and empty amount`() = runTest {
        val recipient = ""
        val amount = ""

        // When
        cut.userDataControl(recipient.isNotEmpty(), amount.isNotEmpty())
        val uiState = cut.uiState.value

        // Then
        uiState.isUserDataReady?.let { assertFalse(it) }
    }

    /**
     * Test the behavior of [userDataControl] when the recipient is provided but the amount is empty.
     * It ensures that the UI state [isUserDataReady] is set to false.
     */
    @Test
    fun `test userDataControl with existed recipient and empty amount`() = runTest {
        val recipient = "r"
        val amount = ""

        // When
        cut.userDataControl(recipient.isNotEmpty(), amount.isNotEmpty())
        val uiState = cut.uiState.value


        // Then
        uiState.isUserDataReady?.let { assertFalse(it) }
    }

    /**
     * Test the behavior of [userDataControl] when the recipient is empty but the amount is provided.
     * It ensures that the UI state [isUserDataReady] is set to false.
     */
    @Test
    fun `test userDataControl with empty recipient and existed amount`() = runTest {
        val recipient = ""
        val amount = "0.0"

        // When
        cut.userDataControl(recipient.isNotEmpty(), amount.isNotEmpty())
        val uiState = cut.uiState.value

        // Then
        uiState.isUserDataReady?.let { assertFalse(it) }
    }

    /**
     * Test the behavior of [userDataControl] when both the recipient and amount are provided.
     * It ensures that the UI state [isUserDataReady] is set to true.
     */
    @Test
    fun `test userDataControl with existed recipient and existed amount`() = runTest {
        val recipient = "r"
        val amount = "0.0"

        // When
        cut.userDataControl(recipient.isNotEmpty(), amount.isNotEmpty())
        val uiState = cut.uiState.value

        // Then
        uiState.isUserDataReady?.let { assertTrue(it) }
    }

    /**
     * Test the behavior of [getAuraTransfer] when the transfer is loading. It ensures that the
     * UI state is updated to reflect that the transfer is in progress (view loading state).
     */
    @Test
    fun `test getAuraTransfer loading`() = runTest {
        // Arrange
        val recipient = "56789"
        val amount = 50.0
        coEvery { dataRepository.getTransfer("testCurrentId", recipient, amount) } returns
                Result.Success(TransferReportModel(done = true))

        // When
        cut.getAuraTransfer(recipient, amount)
        delay(500)

        // Then
        cut.uiState.test {
            val uiStateReady = awaitItem()
            assertEquals(false, uiStateReady.isUserDataReady)
            assertEquals(null, uiStateReady.transferred)
            assertEquals(true, uiStateReady.isViewLoading)
            assertEquals(null, uiStateReady.errorMessage)
        }
    }

    /**
     * Test the behavior of [getAuraTransfer] when the transfer is successful. It ensures that the
     * UI state is updated to indicate that the transfer was successful (transferred = true).
     */
    @Test
    fun `test getAuraTransfer updates UI state on success`() = runTest {
        // Arrange
        val recipient = "56789"
        val amount = 50.0
        coEvery { dataRepository.getTransfer("testCurrentId", recipient, amount) } returns
                Result.Success(TransferReportModel(done = true))

        // When
        cut.getAuraTransfer(recipient, amount)
        delay(1100)

        // Then
        cut.uiState.test {
            val uiStateReady = awaitItem()
            assertEquals(false, uiStateReady.isUserDataReady)
            assertEquals(true, uiStateReady.transferred)
            assertEquals(false, uiStateReady.isViewLoading)
            assertEquals(null, uiStateReady.errorMessage)
        }
    }
    /**
     * Test the behavior of [getAuraTransfer] when the transfer is successful with done failed. It ensures that the
     * UI state is updated to indicate that the transfer was successful (transferred = false).
     */
    @Test
    fun `test getAuraTransfer updates UI state on failed`() = runTest {
        // Arrange
        val recipient = "5678"
        val amount = 10000000.00
        coEvery { dataRepository.getTransfer("testCurrentId", recipient, amount) } returns
                Result.Success(TransferReportModel(done = false))

        // When
        cut.getAuraTransfer(recipient, amount)
        delay(1100)

        // Then
        cut.uiState.test {
            val uiStateReady = awaitItem()
            assertEquals(false, uiStateReady.isUserDataReady)
            assertEquals(false, uiStateReady.transferred)
            assertEquals(false, uiStateReady.isViewLoading)
            assertEquals(null, uiStateReady.errorMessage)
        }
    }

    /**
     * Test the behavior of [getAuraTransfer] when the transfer fails. It ensures that the
     * UI state is updated to reflect the error message when the transfer is unsuccessful.
     */
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
        delay(1100)

        // Then
        cut.uiState.test {
            val uiStateReady = awaitItem()
            assertEquals(false, uiStateReady.isUserDataReady)
            assertEquals(null, uiStateReady.transferred)
            assertEquals(false, uiStateReady.isViewLoading)
            assertEquals(errorMessage, uiStateReady.errorMessage)
        }
    }

    /**
     * Test the [reset] method to ensure that the UI state is cleared correctly, resetting values
     * like [transferred] and [errorMessage] to null.
     */
    @Test
    fun `test reset clears the UI state`() = runTest {
        // Set some initial values in the UI state
        cut._uiState.update {
            it.copy(
                transferred = true,
                isViewLoading = true,
                isUserDataReady = true,
                errorMessage = "Some Error"
            )
        }

        // Call the reset() method
        cut.reset()

        // Access the UI state directly using the value property
        val uiState = cut.uiState.value

        // Assert that the UI state has been reset to null values
        assertNull(uiState.transferred)
        assertNull(uiState.isViewLoading)
        assertNull(uiState.isUserDataReady)
        assertNull(uiState.errorMessage)
    }
}
