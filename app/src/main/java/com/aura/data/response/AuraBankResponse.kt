package com.aura.data.response

import android.content.Context
import android.util.Log
import com.aura.R
import com.aura.domain.model.AccountsReportModel
import com.aura.domain.model.LoginReportModel
import com.aura.domain.model.TransferReportModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

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
    @Json(name = "id")
    val id: String,
    @Json(name = "main")
    val main: Boolean,
    @Json(name = "balance")
    val balance: Double
)

@JsonClass(generateAdapter = true)
data class TransferBankResponse(
    @Json(name = "result")
    val done: Boolean
) {
    fun toDomainModel(context: Context): TransferReportModel {
        val message: String? =
            if (done) null else context.getString(R.string.transfer_error)
        return TransferReportModel(done, message)
    }
}
