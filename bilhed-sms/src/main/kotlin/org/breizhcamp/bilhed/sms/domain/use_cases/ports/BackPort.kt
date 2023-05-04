package org.breizhcamp.bilhed.sms.domain.use_cases.ports

import java.util.UUID

interface BackPort {

    fun ackSmsSent(id: UUID, error: String? = null)

}