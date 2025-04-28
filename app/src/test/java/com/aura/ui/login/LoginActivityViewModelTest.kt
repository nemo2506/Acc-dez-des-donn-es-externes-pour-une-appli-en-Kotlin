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

/**
 * Unit tests for the [LoginActivityViewModel] class, ensuring that its methods
 * and behavior work correctly under various scenarios. This includes testing the UI state
 * changes, interaction with the repository, and handling of different login outcomes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginActivityViewModelTest {

    private lateinit var dataRepository: BankRepository
    private lateinit var cut: LoginActivityViewModel
    private val testDispatcher = StandardTestDispatcher() // To control coroutine execution

    /**
     * Set up the test environment, initializing the required components like
     * the repository and the view model. This method is executed before each test.
     */
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set the main dispatcher for testing
        dataRepository = mockk()
        cut = LoginActivityViewModel(dataRepository)
    }

    /**
     * Reset the dispatcher after tests to ensure no state leaks between tests.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the dispatcher after tests
    }

    /**
     * Test to verify that the initial UI state of the [LoginActivityViewModel] is
     * correctly set to default values.
     */
    @Test
    fun `test initial uiState is default`() = runTest {
        // When
        val uiState = cut.uiState.value

        // Then
        assertNull(uiState.isUserDataReady)
        assertNull(uiState.logged)
        assertNull(uiState.isViewLoading)
        assertNull(uiState.errorMessage)
    }

    /**
     * Test the [userDataControl] method when both the identifier and password are empty.
     * This should set [isUserDataReady] to false.
     */
    @Test
    fun `test userDataControl with empty identifier and empty password`() = runTest {
        val identifier = ""
        val password = ""

        // When
        cut.userDataControl(identifier.isNotEmpty(), password.isNotEmpty())
        val uiState = cut.uiState.value

        // Then
        uiState.isUserDataReady?.let { assertFalse(it) }
    }

    /**
     * Test the [userDataControl] method when the identifier is valid but the password is empty.
     * This should set [isUserDataReady] to false.
     */
    @Test
    fun `test userDataControl with existed identifier and empty password`() = runTest {
        val identifier = "i"
        val password = ""

        // When
        cut.userDataControl(identifier.isNotEmpty(), password.isNotEmpty())
        val uiState = cut.uiState.value

        // Then
        uiState.isUserDataReady?.let { assertFalse(it) }
    }

    /**
     * Test the [userDataControl] method when the identifier is empty but the password is valid.
     * This should set [isUserDataReady] to false.
     */
    @Test
    fun `test userDataControl with empty identifier and existed password`() = runTest {
        val identifier = ""
        val password = "p"

        // When
        cut.userDataControl(identifier.isNotEmpty(), password.isNotEmpty())
        val uiState = cut.uiState.value


        // Then
        uiState.isUserDataReady?.let { assertFalse(it) }
    }

    /**
     * Test the [userDataControl] method when both the identifier and password are valid.
     * This should set [isUserDataReady] to true.
     */
    @Test
    fun `test userDataControl with existed identifier and existed password`() = runTest {
        val identifier = "i"
        val password = "p"

        // When
        cut.userDataControl(identifier.isNotEmpty(), password.isNotEmpty())
        val uiState = cut.uiState.value

        // Then
        uiState.isUserDataReady?.let { assertTrue(it) }
    }

    /**
     * Test the behavior of [getAuraLogin] when the login is loading. This ensures the
     * UI state is updated to reflect that the login request is in progress.
     */
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
        delay(500)

        // Then
        cut.uiState.test {
            val expectedState = QueryUiState(isUserDataReady=false, logged=null, isViewLoading=true, errorMessage=null)
            assertEquals(expectedState, awaitItem())
        }
    }

    /**
     * Test the behavior of [getAuraLogin] when the login is successful. This ensures the
     * UI state is updated to reflect the successful login and sets [logged] to true.
     */
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
        delay(1100)

        // Then
        cut.uiState.test {
            val expectedState = QueryUiState(isUserDataReady=false, logged=true, isViewLoading=false, errorMessage=null)
            assertEquals(expectedState, awaitItem())
        }
    }

    /**
     * Test the behavior of [getAuraLogin] when the login fails. This ensures the
     * UI state is updated to reflect the error and sets [errorMessage] accordingly.
     */
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
        delay(1100)

        // Then
        cut.uiState.test {
            val expectedState = QueryUiState(isUserDataReady=false, logged=false, isViewLoading=false, errorMessage=errorMessage)
            assertEquals(expectedState, awaitItem())
        }
    }

    /**
     * Test the [reset] method to verify that the UI state is cleared correctly.
     * This resets values like [isUserDataReady], [logged], and [errorMessage].
     */
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

        // When
        cut.reset()
        val uiState = cut.uiState.value

        // Then
        assertNull(uiState.isUserDataReady)
        assertNull(uiState.logged)
        assertNull(uiState.errorMessage)
    }
}
