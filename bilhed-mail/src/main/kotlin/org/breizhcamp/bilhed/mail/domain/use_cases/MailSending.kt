package org.breizhcamp.bilhed.mail.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.mail.domain.entities.Mail
import org.breizhcamp.bilhed.mail.domain.use_cases.port.EmailSenderPort
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class MailSending(
    private val emailSenderPort: EmailSenderPort,
) {

    fun send(mail: Mail) {
        logger.info { "Sending mail to [${mail.to}] with template [${mail.template}]" }
        emailSenderPort.send(mail)
        logger.info { "Mail sent to [${mail.to}] with template [${mail.template}]" }
    }

}