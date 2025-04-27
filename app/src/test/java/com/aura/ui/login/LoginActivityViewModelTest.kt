package com.aura.ui.login

import com.aura.data.repository.BankRepository
import com.aura.data.repository.Result
import com.aura.domain.model.LoginReportModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class LoginActivityViewModelTest {

    private lateinit var dataRepository: BankRepository
    private lateinit var cut: LoginActivityViewModel

    @Before
    fun setUp() {
        dataRepository = mockk()
        cut = LoginActivityViewModel(dataRepository)
    }

    @Test
    fun `test initial uiState is default`() = runTest {
        val uiState = cut.uiState.value

        assertNull(uiState.isUserDataReady)
        assertNull(uiState.logged)
        assertFalse(uiState.isViewLoading)
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

//    @Test
//    fun `test getAuraLogin success updates logged true`() = runTest {
//        // Given
//        val testId = "testuser"
//        val testPassword = "testpass"
//
//        coEvery { dataRepository.getLogin(testId, testPassword) } returns
//                Result.Success(LoginReportModel(granted = true))
//
//        // When
//        cut.getAuraLogin(testId, testPassword)
//
//        // Then
//        val uiState = cut.uiState.value
//        assertEquals(false, uiState.isUserDataReady)
//        assertEquals(false, uiState.isViewLoading)
//        assertTrue(uiState.logged == true)
//        assertNull(uiState.errorMessage)
//    }

//    @Test
//    fun `test getAuraLogin failure updates errorMessage`() = runTest {
//        // Given
//        val testId = "wronguser"
//        val testPassword = "wrongpass"
//        val errorMessage = "Invalid credentials"
//
//        coEvery { dataRepository.getLogin(testId, testPassword) } returns
//                Result.Failure(errorMessage)
//
//        // When
//        cut.getAuraLogin(testId, testPassword)
//
//        // Then
//        val uiState = cut.uiState.value
//        assertEquals(false, uiState.isUserDataReady)
//        assertEquals(false, uiState.isViewLoading)
//        assertEquals(false, uiState.logged)
//        assertEquals(errorMessage, uiState.errorMessage)
//    }

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