package org.breizhcamp.bilhed.application.dto

data class ParticipantConfirmReq(
    val company: String?,
    val tShirtSize: String,
    val tShirtCut: String?,
    val vegan: Boolean?,
    val meetAndGreet: Boolean,
    val postalCode: String?,
) {
    fun validate() {
        if (listOf("no", "xs", "s", "m", "l", "xl", "xxl", "3xl").none { it == tShirtSize }) {
            throw IllegalArgumentException("La taille de T-Shirt est invalide")
        }
        if (tShirtSize != "no" && listOf("s", "m").none { it == tShirtCut }) {
            throw IllegalArgumentException("La coupe de T-Shirt est invalide")
        }
    }
}
