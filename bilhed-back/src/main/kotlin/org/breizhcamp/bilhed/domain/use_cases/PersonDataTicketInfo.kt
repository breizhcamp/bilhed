package org.breizhcamp.bilhed.domain.use_cases

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.EndInfo
import org.breizhcamp.bilhed.domain.entities.PersonDataTicket
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeeDataPort
import org.breizhcamp.bilhed.domain.use_cases.ports.GroupPort
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.breizhcamp.bilhed.domain.use_cases.ports.TicketPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class PersonDataTicketInfo(
    private val attendeeDataPort: AttendeeDataPort,
    private val ticketPort: TicketPort,
    private val personPort: PersonPort,
    private val groupPort: GroupPort,
) {

    /** Retrieve some infos about the person attendee data and ticket status */
    fun getInfos(id: UUID): PersonDataTicket? {
        if (personPort.get(id).status == PersonStatus.RELEASED) return null

        val hasData = attendeeDataPort.getData(id) != null
        val hasTicket = ticketPort.hasTicket(id)

        val payed = try {
            personPort.get(id).payed
        } catch (e: EntityNotFoundException) {
            false
        }

        val payUrl = try {
            ticketPort.getPayUrl(id)
        } catch (e: EntityNotFoundException) {
            null
        }

        return PersonDataTicket(hasData, hasTicket, payed, payUrl)
    }

    fun getEndInfos(id: UUID): EndInfo {
        val person = personPort.get(id = id)
        val extendedGroup = groupPort.extendedGroupBy(groupId = person.groupId)

        return EndInfo(
            groupPayment = extendedGroup.first.groupPayment,
            nbMembers = extendedGroup.second.size
        )
    }

}