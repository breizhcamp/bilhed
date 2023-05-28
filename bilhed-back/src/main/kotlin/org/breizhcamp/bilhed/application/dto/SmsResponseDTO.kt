package org.breizhcamp.bilhed.application.dto

import java.util.*

data class SmsResponseDTO(
    val id: UUID,
    val template: String,
    val error: String?
)
