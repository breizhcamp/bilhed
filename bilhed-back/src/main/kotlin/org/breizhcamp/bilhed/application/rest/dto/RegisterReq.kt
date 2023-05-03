package org.breizhcamp.bilhed.application.rest.dto

data class RegisterReq(
    val lastname: String,
    val firstname: String,
    val email: String,
    val telephone: String,
) {
    fun validate() {
        if (lastname.isBlank()) throw IllegalArgumentException("Le nom ne peut pas être vide")
        if (firstname.isBlank()) throw IllegalArgumentException("Le prénom ne peut pas être vide")
        if (email.isBlank()) throw IllegalArgumentException("L'email ne peut pas être vide")
        if (telephone.isBlank()) throw IllegalArgumentException("Le telephone ne peut pas être vide")

        if (!email.matches("^[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$".toRegex())) throw IllegalArgumentException("Email invalide")
        if (!telephone.matches("^0[6-7][0-9]{8}".toRegex())) throw IllegalArgumentException("Telephone invalide")
    }

    fun internationalPhone(): String {
        return if (telephone.startsWith("0")) "+33${telephone.substring(1)}" else telephone
    }
}
