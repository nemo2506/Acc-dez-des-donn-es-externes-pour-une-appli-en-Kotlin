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

    private val _uiState = MutableStateFlow(QueryUiState())
    val uiState: StateFlow<QueryUiState> = _uiState.asStateFlow()

    fun loginManage(identifier: Boolean, password: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoginReady = identifier && password
            )
        }
    }

    fun getAuraLogin(currentId: String, password: String) {

        viewModelScope.launch {

            _uiState.update { currentState ->
                currentState.copy(
                    isLoginReady = false,
                    isViewLoading = true,
                    errorMessage = null
                )
            }
            val startTime = System.currentTimeMillis()
            val elapsed = System.currentTimeMillis() - startTime
            val remainingDelay = 1000 - elapsed
            if (remainingDelay > 0) delay(remainingDelay)

            when (val loginUpdate = dataRepository.getLogin(currentId, password)) {

                is Result.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoginReady = false,
                            logged = false,
                            isViewLoading = false,
                            errorMessage = loginUpdate.message
                        )
                    }
                }

                Result.Loading -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoginReady = false,
                            isViewLoading = true,
                            errorMessage = null
                        )
                    }
                }

                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoginReady = false,
                            logged = loginUpdate.value.granted,
                            isViewLoading = false,
                            errorMessage = null
                        )
                    }
                }
            }

        }
    }
}

data class QueryUiState(
    val isLoginReady: Boolean = false,
    val logged: Boolean? = null,
    val isViewLoading: Boolean = false,
    val errorMessage: String? = null
)
