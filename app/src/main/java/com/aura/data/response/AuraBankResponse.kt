package com.aura.data.response

import android.content.Context
import com.aura.R
import com.aura.domain.model.AccountsReportModel
import com.aura.domain.model.LoginReportModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginBankResponse(
    @Json(name = "granted")
    val granted: Boolean
) {
    fun toDomainModel(context: Context): LoginReportModel {
        val message: String =
            if (granted) "connexion réussi" else context.getString(R.string.login_failed)
        return LoginReportModel(granted, message)
    }
}
@JsonClass(generateAdapter = true)
data class AccountBankResponse(
    @Json(name = "list")
    val accounts: List<AccountResponse>,
) {

    @JsonClass(generateAdapter = true)
    data class AccountResponse(
        @Json(name = "id")
        val id: String,
        @Json(name = "main")
        val main: Boolean,
        @Json(name = "balance")
        val balance: Double,
    ) {

        @JsonClass(generateAdapter = true)
        data class TemperatureResponse(
            @Json(name = "temp")
            val temp: Double,
        )
    }

    fun toDomainModel(context: Context): List<AccountsReportModel> {
        return accounts.map { account ->

            AccountsReportModel(
                id = account.id,
                main = account.main,
                balance = account.balance,
                message = "TEST"
            )
        }
    }
}