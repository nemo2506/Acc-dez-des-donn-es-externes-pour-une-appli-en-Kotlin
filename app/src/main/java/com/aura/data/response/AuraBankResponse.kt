package com.aura.data.response

import android.content.Context
import com.aura.R
import com.aura.domain.model.LoginReportModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginBankResponse(
    @Json(name = "granted")
    val granted: Boolean
) {
    fun toDomainModel(context: Context): LoginReportModel {
        val message: String = if (granted) "connexion réussi" else context.getString(R.string.login_failed)
        return LoginReportModel(granted, message)
    }
}
