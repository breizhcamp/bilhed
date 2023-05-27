package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.entities.Sms

interface SmsPort {

    fun send(sms: Sms)
}