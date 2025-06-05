package org.breizhcamp.bilhed.infrastructure.billetweb

import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.Ticket
import org.breizhcamp.bilhed.domain.entities.TicketExportData
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

    override fun create(participants: List<Person>): List<Ticket> {
        return participants.map {
            logger.info { "[TicketConsole] Create ticket for participant [${it.id}] / [${it.lastname}] [${it.firstname}]" }
            Ticket(generatePayUrl(), false)
        }
    }

    override fun delete(attendees: List<Person>) {
        logger.info { "[TicketConsole] Delete ticket for participants: " + attendees.joinToString { "${it.id}: ${it.lastname} ${it.firstname}" } }
    }

    override fun hasTicket(id: UUID): Boolean {
        return false
    }

    override fun getPayUrl(id: UUID): String = generatePayUrl()
    override fun getPayed(): List<UUID> = emptyList()
    override fun getExportList(): List<TicketExportData> {
        return emptyList()
    }

    private fun generatePayUrl() = "${config.participantFrontUrl}/#/ticket?payWithConsole"

}
