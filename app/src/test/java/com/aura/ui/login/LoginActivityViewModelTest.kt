package com.aura.ui.login

import androidx.lifecycle.SavedStateHandle
import com.aura.data.repository.BankRepository
import com.aura.ui.ConstantsApp
import io.mockk.every
import io.mockk.mockk
import io.mockk.stackTracesAlignmentValueOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class LoginActivityViewModelTest {

    private lateinit var dataRepository: BankRepository
//    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        dataRepository = mockk<BankRepository>()
    }

    @Test
    fun getUiState() {
    }

    @Test
    fun `test userDataControl with empty identifier and empty password`() = runTest {
        val identifier = ""
        val password = ""
        val cut = LoginActivityViewModel(dataRepository)
        cut.userDataControl(identifier.isNotEmpty(), password.isNotEmpty())
        val uiState = cut.uiState.value
        uiState.isUserDataReady?.let { assertFalse(it) }
    }

    @Test
    fun `test userDataControl with existed identifier and empty password`() = runTest {
        val identifier = "i"
        val password = ""
        val cut = LoginActivityViewModel(dataRepository)
        cut.userDataControl(identifier.isNotEmpty(), password.isNotEmpty())
        val uiState = cut.uiState.value
        uiState.isUserDataReady?.let { assertFalse(it) }
    }

    @Test
    fun `test userDataControl with empty identifier and existed password`() = runTest {
        val identifier = ""
        val password = "p"
        val cut = LoginActivityViewModel(dataRepository)
        cut.userDataControl(identifier.isNotEmpty(), password.isNotEmpty())
        val uiState = cut.uiState.value
        uiState.isUserDataReady?.let { assertFalse(it) }
    }

    @Test
    fun `test userDataControl with existed identifier and existed password`() = runTest {
        val identifier = "i"
        val password = "p"
        val cut = LoginActivityViewModel(dataRepository)
        cut.userDataControl(identifier.isNotEmpty(), password.isNotEmpty())
        val uiState = cut.uiState.value
        uiState.isUserDataReady?.let { assertTrue(it) }
    }

    @Test
    fun getAuraLogin() {
    }

    @Test
    fun reset() {
    }
}