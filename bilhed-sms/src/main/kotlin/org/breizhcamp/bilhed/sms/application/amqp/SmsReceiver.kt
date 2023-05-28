package org.breizhcamp.bilhed.sms.application.amqp

import mu.KotlinLogging
import org.breizhcamp.bilhed.sms.application.amqp.dto.SmsFromBack
import org.breizhcamp.bilhed.sms.domain.entities.Sms
import org.breizhcamp.bilhed.sms.domain.use_cases.SmsSending
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class SmsReceiver(
    private val smsSending: SmsSending,
) {

    @RabbitListener(queues = ["sms-send-queue"], containerFactory = "smsContainerFactory")
    fun receive(sms: SmsFromBack) {
        logger.info { "Received SMS from backend to [${sms.phone}] with template [${sms.template}] and model: ${sms.model}" }
        smsSending.send(sms.toSms())
    }

}

private fun SmsFromBack.toSms() = Sms(id, phone, template, model)
