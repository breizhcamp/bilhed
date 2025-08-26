package org.breizhcamp.bilhed.application.dto

import java.util.UUID

data class ParticipantConfirmReq(
    val id: UUID,
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
        if (tShirtSize != "no" && listOf("s", "f").none { it == tShirtCut }) {
            throw IllegalArgumentException("La coupe de T-Shirt est invalide")
        }
    }
}
