package org.breizhcamp.bilhed.application.amqp

import mu.KotlinLogging
import org.breizhcamp.bilhed.application.dto.SmsResponseDTO
import org.breizhcamp.bilhed.domain.use_cases.ParticipantNotify
import org.breizhcamp.bilhed.domain.use_cases.Registration
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class SmsResponse(
    private val registration: Registration,
    private val participantNotify: ParticipantNotify,
) {

    @RabbitListener(queues = ["sms-response-queue"], containerFactory = "smsContainerFactory")
    fun receive(response: SmsResponseDTO) {
        logger.info { "Received SMS response from SMS service: $response" }

        when (response.template) {
            "registered_token" -> registration.saveSmsStatus(response.id, response.error)
            "draw_success" -> participantNotify.saveSmsStatus(response.id, response.error)
            "payed_reminder" -> Unit
            else -> logger.info { "Unknown handling for template: ${response.template}" }
        }
    }

}