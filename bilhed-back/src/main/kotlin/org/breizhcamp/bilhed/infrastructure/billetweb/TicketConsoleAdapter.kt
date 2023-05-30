package org.breizhcamp.bilhed.infrastructure.billetweb

import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.entities.PayStatus
import org.breizhcamp.bilhed.domain.entities.Ticket
import org.breizhcamp.bilhed.domain.use_cases.ports.TicketPort
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
@ConditionalOnProperty(prefix = "bilhed.back", name = ["billetWeb.enabled"], havingValue = "false")
class TicketConsoleAdapter(
    private val config: BilhedBackConfig,
): TicketPort {

    @PostConstruct
    fun setup() {
        logger.info { "[TicketConsole] Using console for ticket creation" }
    }

    override fun create(participant: Participant): Ticket {
        logger.info { "[TicketConsole] Create ticket for participant [${participant.id}] / [${participant.lastname}] [${participant.firstname}]" }
        return Ticket(config.participantFrontUrl, PayStatus.TO_PAY)
    }

}