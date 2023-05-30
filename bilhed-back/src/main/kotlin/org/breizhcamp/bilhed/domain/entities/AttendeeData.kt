package org.breizhcamp.bilhed.domain.entities

data class AttendeeData(
    val company: String?,
    val tShirtSize: String,
    val tShirtCut: String?,
    val vegan: Boolean,
    val meetAndGreet: Boolean,
    val postalCode: String?,
)
