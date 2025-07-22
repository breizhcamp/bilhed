package org.breizhcamp.bilhed.application.dto.admin

import kotlin.text.matches

class UpdateContactReq(
    val email: String,
    val telephone: String?,
) {
    fun validate() {
        if (email.isBlank()) throw IllegalArgumentException("L'email doit être renseigné")
        if (!email.matches("^[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$".toRegex())) throw IllegalArgumentException("Email invalide")

        if (telephone != null && telephone.isNotBlank()) {
            if (!telephone.matches("^0[6-7][0-9]{8}".toRegex())) throw IllegalArgumentException("Téléphone invalide")
        }
    }
}