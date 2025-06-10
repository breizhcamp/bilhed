package org.breizhcamp.bilhed.application.dto

open class CompanionRegisterReq(
    val lastname: String,
    val firstname: String,
    val telephone: String?,
    val email: String,
) {
    open fun validate(groupPayment: Boolean, ref: Boolean = false) {
        if (lastname.isBlank()) throw IllegalArgumentException("Le nom ne peut pas être vide")
        if (firstname.isBlank()) throw IllegalArgumentException("Le prénom ne peut pas être vide")
        if (email.isBlank()) throw IllegalArgumentException("L'email ne peut pas être vide")

        if (!email.matches("^[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$".toRegex())) throw IllegalArgumentException("Email invalide")

        if (!groupPayment || ref) {
            validatePhone()
        }
    }

    fun internationalPhone(): String? {
        return if (telephone?.startsWith("0") == true) "+33${telephone.substring(1)}" else telephone
    }

    open fun validatePhone() {
        if (telephone == null) throw IllegalArgumentException("En cas de paiement séparé, le téléphone ne peut pas être vide.")

        if (telephone.isBlank()) throw IllegalArgumentException("Le telephone ne peut pas être vide")

        if (!telephone.matches("^0[6-7][0-9]{8}".toRegex())) throw IllegalArgumentException("Telephone invalide")
    }
}
