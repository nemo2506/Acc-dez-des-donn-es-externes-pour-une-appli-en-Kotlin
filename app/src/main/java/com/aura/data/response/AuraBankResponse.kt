package com.aura.data.response

import com.aura.domain.model.Account
import com.aura.domain.model.LoginReportModel
import com.aura.domain.model.TransferReportModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Return api result to login in report
 */
@JsonClass(generateAdapter = true)
data class LoginBankResponse(
    @Json(name = "granted")
    val granted: Boolean
) {
    fun toDomainModel(): LoginReportModel {
        return LoginReportModel(granted)
    }
}

/**
 * Return api result to balance in report
 */
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

/**
 * Return api result to transfer in report
 */
@JsonClass(generateAdapter = true)
data class TransferBankResponse(
    @Json(name = "result")
    val done: Boolean
) {
    fun toDomainModel(): TransferReportModel {
        return TransferReportModel(done)
    }
}
