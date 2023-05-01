package org.breizhcamp.bilhed.sms.domain.use_cases.ports

import org.breizhcamp.bilhed.sms.domain.entities.Sms

interface SmsPort {

    fun send(sms: Sms)

}