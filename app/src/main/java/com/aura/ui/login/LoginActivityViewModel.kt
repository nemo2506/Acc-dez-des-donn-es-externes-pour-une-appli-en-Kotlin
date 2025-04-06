package com.aura.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import com.aura.R
import com.aura.data.repository.BankRepository
import com.aura.data.repository.Result
import com.aura.domain.model.BalanceReportModel
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
        return when (val result = dataRepository.getLogin(id, password)) {
            is Result.Failure -> LoginReportModel(false, context.getString(R.string.login_failed))
            Result.Loading -> LoginReportModel(false, context.getString(R.string.loading))
            is Result.Success -> result.value
        }
    }

    suspend fun getAuraBalance(): BalanceReportModel {
        return when (val result = dataRepository.getAccounts()) {
            is Result.Failure -> BalanceReportModel(null, context.getString(R.string.balance_error))
            Result.Loading -> BalanceReportModel(null, context.getString(R.string.loading))
            is Result.Success -> result.value
        }
    }
}