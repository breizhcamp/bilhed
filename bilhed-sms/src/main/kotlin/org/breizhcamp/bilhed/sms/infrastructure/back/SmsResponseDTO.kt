package org.breizhcamp.bilhed.sms.infrastructure.back

import java.util.*

data class SmsResponseDTO(
    val id: UUID,
    val error: String?
)
