package org.breizhcamp.bilhed.sms.domain.use_cases.ports

import java.util.UUID

interface BackPort {

    fun ackSmsSent(id: UUID, template: String, error: String? = null)

}