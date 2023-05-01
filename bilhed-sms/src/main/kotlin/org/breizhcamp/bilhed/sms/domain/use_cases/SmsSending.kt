package org.breizhcamp.bilhed.sms.domain.use_cases

import org.breizhcamp.bilhed.sms.domain.entities.Sms
import org.breizhcamp.bilhed.sms.domain.use_cases.ports.SmsPort
import org.springframework.stereotype.Service

@Service
class SmsSending(
    private val smsPort: SmsPort,
) {

    fun send(sms: Sms) {
        smsPort.send(sms)
    }

}