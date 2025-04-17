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


@HiltViewModel
class LoginActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {

    /**
     * StateFlow to inform screen target activity
     */
    private val _uiState = MutableStateFlow(QueryUiState())
    val uiState: StateFlow<QueryUiState> = _uiState.asStateFlow()

    /**
     * State Update to inform user data verified
     */
    fun userDataControl(identifier: Boolean, password: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isUserDataReady = identifier && password
            )
        }
    }

    /**
     * State Update target to transfer verified
     */
    fun getAuraLogin(currentId: String, password: String) {

        viewModelScope.launch {

            /**
             * Force to wait 1 sec to display loader
             */
            _uiState.update { currentState ->
                currentState.copy(
                    isUserDataReady = false,
                    isViewLoading = true,
                    errorMessage = null
                )
            }
            val startTime = System.currentTimeMillis()
            val elapsed = System.currentTimeMillis() - startTime
            val remainingDelay = 1000 - elapsed
            if (remainingDelay > 0) delay(remainingDelay)

            /**
             * Update stateflow in case failure, loading, success
             */
            when (val loginUpdate = dataRepository.getLogin(currentId, password)) {

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

                Result.Loading -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isUserDataReady = false,
                            isViewLoading = true,
                            errorMessage = null
                        )
                    }
                }

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
     * State Update to reset stateflow when failed
     */
    fun reset() {
        _uiState.update { currentState ->
            currentState.copy(
                isUserDataReady = null,
                logged = null
            )
        }
    }
}

/**
 * Data to query UserData, Logged, IsLoading and ErrorMessage
 */
data class QueryUiState(
    val isUserDataReady: Boolean? = null,
    val logged: Boolean? = null,
    val isViewLoading: Boolean? = null,
    val errorMessage: String? = null
)
