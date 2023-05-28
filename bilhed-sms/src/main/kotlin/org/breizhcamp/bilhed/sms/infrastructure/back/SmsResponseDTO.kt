package org.breizhcamp.bilhed.sms.infrastructure.back

import java.util.*

data class SmsResponseDTO(
    val id: UUID,
    val template: String,
    val error: String?
)
