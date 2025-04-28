package com.aura.ui.login

import app.cash.turbine.test
import app.cash.turbine.testIn
import app.cash.turbine.turbineScope
import com.aura.data.repository.BankRepository
import com.aura.data.repository.Result
import com.aura.domain.model.LoginReportModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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
class LoginActivityViewModelTest {

    private lateinit var dataRepository: BankRepository
    private lateinit var cut: LoginActivityViewModel
    private val testDispatcher = StandardTestDispatcher() // To control coroutine execution

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set the main dispatcher for testing
        dataRepository = mockk()
        cut = LoginActivityViewModel(dataRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the dispatcher after tests
    }

    @Test
    fun `test initial uiState is default`() = runTest {
        val uiState = cut.uiState.value

        assertNull(uiState.isUserDataReady)
        assertNull(uiState.logged)
        assertNull(uiState.isViewLoading)
        assertNull(uiState.errorMessage)
    }

    @Test
    fun `test userDataControl with empty identifier and empty password`() = runTest {
        val identifier = ""
        val password = ""
        cut.userDataControl(identifier.isNotEmpty(), password.isNotEmpty())
        val uiState = cut.uiState.value
        uiState.isUserDataReady?.let { assertFalse(it) }
    }

    @Test
    fun `test userDataControl with existed identifier and empty password`() = runTest {
        val identifier = "i"
        val password = ""
        cut.userDataControl(identifier.isNotEmpty(), password.isNotEmpty())
        val uiState = cut.uiState.value
        uiState.isUserDataReady?.let { assertFalse(it) }
    }

    @Test
    fun `test userDataControl with empty identifier and existed password`() = runTest {
        val identifier = ""
        val password = "p"
        cut.userDataControl(identifier.isNotEmpty(), password.isNotEmpty())
        val uiState = cut.uiState.value
        uiState.isUserDataReady?.let { assertFalse(it) }
    }

    @Test
    fun `test userDataControl with existed identifier and existed password`() = runTest {
        val identifier = "i"
        val password = "p"
        cut.userDataControl(identifier.isNotEmpty(), password.isNotEmpty())
        val uiState = cut.uiState.value
        uiState.isUserDataReady?.let { assertTrue(it) }
    }

    @Test
    fun `test getAuraLogin loading`() = runTest {
        // Given
        val testId = "identifier"
        val testPassword = "password"

        // Mocking repository response
        coEvery { dataRepository.getLogin(testId, testPassword) } returns
                Result.Success(LoginReportModel(granted = true))

        // When
        cut.getAuraLogin(testId, testPassword)

        // Then
        delay(500)
        cut.uiState.test {
            val expectedState = QueryUiState(isUserDataReady=false, logged=null, isViewLoading=true, errorMessage=null)
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `test getAuraLogin success updates logged true`() = runTest {
        // Given
        val testId = "identifier"
        val testPassword = "password"

        // Mocking repository response
        coEvery { dataRepository.getLogin(testId, testPassword) } returns
                Result.Success(LoginReportModel(granted = true))

        // When
        cut.getAuraLogin(testId, testPassword)

        // Then
        delay(1100)
        cut.uiState.test {
            val expectedState = QueryUiState(isUserDataReady=false, logged=true, isViewLoading=false, errorMessage=null)
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `test getAuraLogin failure updates errorMessage`() = runTest {
        // Given
        val testId = "wronguser"
        val testPassword = "wrongpass"
        val errorMessage = "Invalid credentials"

        coEvery { dataRepository.getLogin(testId, testPassword) } returns
                Result.Failure(errorMessage)

        // When
        cut.getAuraLogin(testId, testPassword)

        // Then
        delay(1100)
        cut.uiState.test {
            val expectedState = QueryUiState(isUserDataReady=false, logged=false, isViewLoading=false, errorMessage=errorMessage)
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `test reset clears the UI state`() = runTest {
        // Set some initial values in the UI state
        cut._uiState.update {
            it.copy(
                isUserDataReady = true,
                logged = true,
                errorMessage = "Some Error"
            )
        }

        // Call the reset() method
        cut.reset()

        // Access the UI state directly using the value property
        val uiState = cut.uiState.value

        // Assert that the UI state has been reset to null values
        assertNull(uiState.isUserDataReady)
        assertNull(uiState.logged)
        assertNull(uiState.errorMessage)
    }
}