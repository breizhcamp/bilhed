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
import java.util.*

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

    override fun create(participants: List<Participant>): List<Ticket> {
        return participants.map {
            logger.info { "[TicketConsole] Create ticket for participant [${it.id}] / [${it.lastname}] [${it.firstname}]" }
            Ticket(generatePayUrl(), PayStatus.TO_PAY)
        }
    }

    override fun getPayUrl(id: UUID): String = generatePayUrl()
    override fun getPayed(): List<UUID> = emptyList()

    private fun generatePayUrl() = "${config.participantFrontUrl}/#/ticket"

}