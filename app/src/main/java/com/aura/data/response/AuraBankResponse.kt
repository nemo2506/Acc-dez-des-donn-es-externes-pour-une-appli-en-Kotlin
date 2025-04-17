package com.aura.data.response

import com.aura.domain.model.Account
import com.aura.domain.model.LoginReportModel
import com.aura.domain.model.TransferReportModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Represents the API response for a login attempt.
 *
 * @property granted Indicates whether the login was successful.
 */
@JsonClass(generateAdapter = true)
data class LoginBankResponse(
    @Json(name = "granted")
    val granted: Boolean
) {
    /**
     * Converts the API response model to the domain model [LoginReportModel].
     *
     * @return A domain-level representation of the login result.
     */
    fun toDomainModel(): LoginReportModel {
        return LoginReportModel(granted)
    }
}

/**
 * Represents the API response for an account balance query.
 *
 * @property id The unique identifier of the account.
 * @property main Indicates whether this is the user's primary account.
 * @property balance The available balance in the account.
 */
@JsonClass(generateAdapter = true)
data class AccountBankResponse(
    @Json(name = "id")
    val id: String,
    @Json(name = "main")
    val main: Boolean,
    @Json(name = "balance")
    val balance: Double
) {
    /**
     * Converts the API response model to the domain model [Account].
     *
     * @return A domain-level representation of the account.
     */
    fun toDomainModel(): Account {
        return Account(id, main, balance)
    }
}

/**
 * Represents the API response for a money transfer operation.
 *
 * @property done Indicates whether the transfer was successful.
 */
@JsonClass(generateAdapter = true)
data class TransferBankResponse(
    @Json(name = "result")
    val done: Boolean
) {
    /**
     * Converts the API response model to the domain model [TransferReportModel].
     *
     * @return A domain-level representation of the transfer result.
     */
    fun toDomainModel(): TransferReportModel {
        return TransferReportModel(done)
    }
}
