package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.entities.Sms

interface SmsPort {
    fun sendRegistered(registered: Registered)

    fun send(sms: Sms)
}