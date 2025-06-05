package org.breizhcamp.bilhed.application.dto.admin

class UpdateEmailReq(
    val email: String,
) {
    fun validate() {
        if (email.isBlank()) throw IllegalArgumentException("L'email et le téléphone doivent être renseignés")
        if (!email.matches("^[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$".toRegex())) throw IllegalArgumentException("Email invalide")
    }
}