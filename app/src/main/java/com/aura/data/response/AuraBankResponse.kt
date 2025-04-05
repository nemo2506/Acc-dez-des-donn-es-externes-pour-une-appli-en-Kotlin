package com.aura.data.response

import com.aura.domain.model.LoginReportModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginBankResponse(
    @Json(name = "granted")
    val granted: Boolean
) {
    fun toDomainModel(): LoginReportModel {
        val message: String = if (granted) "connexion réussi" else "Identifiant ou Mot de Passe Invalide"
        return LoginReportModel(granted, message)
    }
}
