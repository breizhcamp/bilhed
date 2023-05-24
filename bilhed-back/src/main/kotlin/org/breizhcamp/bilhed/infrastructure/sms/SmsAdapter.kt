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

    override fun sendRegistered(registered: Registered) {
        logger.info { "Sending SMS to AMQP to [${registered.lastname} ${registered.firstname}] / " +
                "[${registered.telephone}] with token [${registered.token}]" }

        rabbitTemplate.convertAndSend("sms-send", "", Sms(
            id = registered.id,
            phone = registered.telephone,
            message = "Votre code pour l'inscription à la loterie du BreizhCamp est ${registered.token}"
        )
        )
    }

    override fun send(sms: Sms) {
        TODO("Not yet implemented")
    }
}