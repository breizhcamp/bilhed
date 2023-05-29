package org.breizhcamp.bilhed.sms.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.sms.domain.entities.Sms
import org.breizhcamp.bilhed.sms.domain.use_cases.ports.BackPort
import org.breizhcamp.bilhed.sms.domain.use_cases.ports.SmsPort
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class SmsSending(
    private val smsPort: SmsPort,
    private val backPort: BackPort,
    private val templateEngine: SpringTemplateEngine,
) {

    fun send(sms: Sms) {
        try {
            // rate limiting
            Thread.sleep(100)
            val ctx = Context(Locale.FRANCE, sms.model)
            val message = templateEngine.process("sms/${sms.template}.txt", ctx)

            logger.info { "Sending SMS to [${sms.phone}] with message [${message}]" }
            smsPort.send(sms.phone, message, sms.template)
            backPort.ackSmsSent(sms.id, sms.template)
            logger.info { "SMS sent to [${sms.phone}]" }

        } catch (e: Exception) {
            logger.error(e) { "Error while sending SMS to [${sms.phone}]" }
            backPort.ackSmsSent(sms.id, sms.template, e.message)
        }
    }

}