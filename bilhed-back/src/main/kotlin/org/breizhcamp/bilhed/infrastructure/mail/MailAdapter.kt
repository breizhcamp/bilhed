package org.breizhcamp.bilhed.infrastructure.mail

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.entities.Mail
import org.breizhcamp.bilhed.domain.use_cases.ports.MailPort
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class MailAdapter(
    private val rabbitTemplate: RabbitTemplate,
): MailPort {

    override fun send(mail: Mail) {
        val mailMq = mail.toMQ()
        logger.info { "Sending mail to [${mailMq.to}] with template [${mailMq.template}]" }
        rabbitTemplate.convertAndSend("mail-send", "", mailMq)
    }
}