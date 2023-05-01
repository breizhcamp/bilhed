package org.breizhcamp.bilhed.sms.application.amqp

import mu.KotlinLogging
import org.breizhcamp.bilhed.sms.application.amqp.dto.SmsFromBack
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class SmsReceiver {

    @RabbitListener(queues = ["sms-send-queue"], containerFactory = "smsContainerFactory")
    fun receive(sms: SmsFromBack) {
        logger.info { "Received sms to ${sms.phone} with message ${sms.message}" }
    }

}