package org.breizhcamp.bilhed.application.dto.admin

data class ParticipantDTO(
    val id: String,
    val lastname: String,
    val firstname: String,
    val email: String,
    val telephone: String,
    val pass: String,
    val kids: String?
)
