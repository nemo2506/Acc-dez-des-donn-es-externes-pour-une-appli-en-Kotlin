package com.aura.ui.login

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
import javax.inject.Inject


@HiltViewModel
class LoginActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QueryUiState())
    val uiState: StateFlow<QueryUiState> = _uiState.asStateFlow()

    fun getAuraLogin(currentId: String, password: String) {

        viewModelScope.launch {

            when (val loginUpdate = dataRepository.getLogin(currentId, password)) {
                is Result.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            logged = false,
                            isViewLoading = false,
                            errorMessage = loginUpdate.message
                        )
                    }
                }

                Result.Loading -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isViewLoading = true,
                            errorMessage = null
                        )
                    }
                }

                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
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
    val logged: Boolean? = null,
    val isViewLoading: Boolean = false,
    val errorMessage: String? = null
)
