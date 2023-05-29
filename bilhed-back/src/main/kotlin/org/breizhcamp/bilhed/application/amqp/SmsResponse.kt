package org.breizhcamp.bilhed.application.amqp

import mu.KotlinLogging
import org.breizhcamp.bilhed.application.dto.SmsResponseDTO
import org.breizhcamp.bilhed.domain.use_cases.ParticipantNotif
import org.breizhcamp.bilhed.domain.use_cases.Registration
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class SmsResponse(
    private val registration: Registration,
    private val participantNotif: ParticipantNotif,
) {

    @RabbitListener(queues = ["sms-response-queue"], containerFactory = "smsContainerFactory")
    fun receive(response: SmsResponseDTO) {
        logger.info { "Received SMS response from SMS service: $response" }

        when (response.template) {
            "register" -> registration.saveSmsStatus(response.id, response.error)
            "draw_success" -> participantNotif.saveSmsStatus(response.id, response.error)
            else -> logger.info { "Unknown handling for template: ${response.template}" }
        }
    }

}