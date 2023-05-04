package org.breizhcamp.bilhed.sms.infrastructure.back

import org.breizhcamp.bilhed.sms.domain.use_cases.ports.BackPort
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class BackAdapter(
    private val rabbitTemplate: RabbitTemplate,
): BackPort {

    override fun ackSmsSent(id: UUID, error: String?) {
        rabbitTemplate.convertAndSend("sms-response", "", SmsResponseDTO(id, error))
    }
}