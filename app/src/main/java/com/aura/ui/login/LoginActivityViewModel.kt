package com.aura.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import com.aura.R
import com.aura.data.repository.BankRepository
import com.aura.data.repository.Result
import com.aura.domain.model.LoginReportModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@HiltViewModel
class LoginActivityViewModel @Inject constructor(
    private val dataRepository: BankRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    suspend fun getAuraLogin(id: String, password: String): LoginReportModel {
        return when (val result = dataRepository.fetchLoginAccess(id, password)) {
            is Result.Success -> result.value
            is Result.Failure -> LoginReportModel(false, context.getString(R.string.login_failed))
            is Result.Loading -> LoginReportModel(false, context.getString(R.string.server_error))
        }
    }
}