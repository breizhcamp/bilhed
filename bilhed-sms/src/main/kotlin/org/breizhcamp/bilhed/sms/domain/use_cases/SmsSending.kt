package org.breizhcamp.bilhed.sms.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.sms.domain.entities.Sms
import org.breizhcamp.bilhed.sms.domain.use_cases.ports.BackPort
import org.breizhcamp.bilhed.sms.domain.use_cases.ports.SmsPort
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class SmsSending(
    private val smsPort: SmsPort,
    private val backPort: BackPort,
) {

    fun send(sms: Sms) {
        try {
            logger.info { "Sending SMS to [${sms.phone}] with message [${sms.message}]" }
            smsPort.send(sms)
            backPort.ackSmsSent(sms.id)
            logger.info { "SMS sent to [${sms.phone}]" }

        } catch (e: Exception) {
            logger.error(e) { "Error while sending SMS to [${sms.phone}]" }
            backPort.ackSmsSent(sms.id, e.message)
        }
    }

}