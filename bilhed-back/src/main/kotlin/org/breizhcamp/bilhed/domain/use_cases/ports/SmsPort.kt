package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.infrastructure.db.model.SmsMQ

interface SmsPort {

    fun send(sms: SmsMQ)
}