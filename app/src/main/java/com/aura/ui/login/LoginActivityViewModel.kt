package com.aura.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.repository.BankRepository
import com.aura.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * ViewModel responsible for handling the login logic of the user.
 *
 * Validates user credentials, manages UI states for loading, error, and login status,
 * and communicates with the repository for authentication.
 */
@HiltViewModel
class LoginActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {

    /**
     * StateFlow used to represent the UI state in the login screen.
     * It holds information such as whether user data is ready, whether the login is successful,
     * and any error messages encountered.
     */
    private val _uiState = MutableStateFlow(QueryUiState())
    val uiState: StateFlow<QueryUiState> = _uiState.asStateFlow()

    /**
     * Updates the UI state to reflect whether the user has provided valid data
     * (non-empty identifier and password).
     *
     * @param identifier Boolean indicating if the identifier is not empty.
     * @param password Boolean indicating if the password is not empty.
     */
    fun userDataControl(identifier: Boolean, password: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isUserDataReady = identifier && password
            )
        }
    }

    /**
     * Initiates the login process by communicating with the repository.
     * It updates the UI state based on the result of the login attempt.
     *
     * @param currentId The user's identifier (username).
     * @param password The user's password.
     */
    fun getAuraLogin(currentId: String, password: String) {

        viewModelScope.launch {

            // Simulate a delay before showing the loader
            _uiState.update { currentState ->
                currentState.copy(
                    isUserDataReady = false,
                    isViewLoading = true,
                    errorMessage = null
                )
            }
            // Force to wait 1 second to display the loader
            delay(1000)

            // Attempt to log in and update UI state based on the result
            when (val loginUpdate = dataRepository.getLogin(currentId, password)) {

                // If login fails, update state with failure message
                is Result.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isUserDataReady = false,
                            logged = false,
                            isViewLoading = false,
                            errorMessage = loginUpdate.message
                        )
                    }
                }

                // If login is successful, update state with login success
                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isUserDataReady = false,
                            logged = loginUpdate.value.granted,
                            isViewLoading = false,
                            errorMessage = null
                        )
                    }
                }
            }

        }
    }

    /**
     * Resets the UI state in case of a login failure.
     * Clears the user data readiness, login status, and other state variables.
     */
    fun reset() {
        _uiState.update { currentState ->
            currentState.copy(
                isUserDataReady = null,
                logged = null,
                errorMessage = null
            )
        }
    }
}

/**
 * Data class that represents the UI state for the login screen.
 *
 * Holds information about whether the user data is ready,
 * whether the login was successful, if the screen is loading,
 * and any error messages that may have occurred during login.
 */
data class QueryUiState(
    val isUserDataReady: Boolean? = null,
    val logged: Boolean? = null,
    val isViewLoading: Boolean = false,
    val errorMessage: String? = null
)
