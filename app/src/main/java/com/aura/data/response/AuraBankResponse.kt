package com.aura.data.response

import com.aura.domain.model.Account
import com.aura.domain.model.LoginReportModel
import com.aura.domain.model.TransferReportModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginBankResponse(
    @Json(name = "granted")
    val granted: Boolean
) {
    fun toDomainModel(): LoginReportModel {
        val message: String =
            if (granted) "connexion réussi" else ""
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
){
    fun toDomainModel(): Account {
        return Account(id, main, balance)
    }
}

@JsonClass(generateAdapter = true)
data class TransferBankResponse(
    @Json(name = "result")
    val done: Boolean
) {
    fun toDomainModel(): TransferReportModel {
        return TransferReportModel(done, null)
    }
}
