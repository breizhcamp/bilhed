package org.breizhcamp.bilhed.application.dto.admin

class UpdateContactReq(
    val email: String,
    val telephone: String
) {
    fun validate() {
        if (email.isBlank() || telephone.isBlank()) throw IllegalArgumentException("L'email et le téléphone doivent être renseignés")
        if (!email.matches("^[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$".toRegex())) throw IllegalArgumentException("Email invalide")
        if (!telephone.matches("^\\+33[6-7][0-9]{8}".toRegex())) throw IllegalArgumentException("Telephone invalide")
    }
}