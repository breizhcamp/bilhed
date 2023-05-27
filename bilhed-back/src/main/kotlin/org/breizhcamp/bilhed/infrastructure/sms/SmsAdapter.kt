package org.breizhcamp.bilhed.infrastructure.sms

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.entities.Sms
import org.breizhcamp.bilhed.domain.use_cases.ports.SmsPort
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class SmsAdapter(
    private val rabbitTemplate: RabbitTemplate,
): SmsPort {

    override fun send(sms: Sms) {
        logger.info { "Sending SMS to AMQP to [${sms.phone}] with template [${sms.template}] and model: ${sms.model}" }
        rabbitTemplate.convertAndSend("sms-send", "", sms)
    }
}