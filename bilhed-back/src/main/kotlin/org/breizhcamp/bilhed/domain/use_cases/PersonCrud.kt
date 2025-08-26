package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.application.dto.admin.UpdateContactReq
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.PersonComplete
import org.breizhcamp.bilhed.domain.entities.PersonFilter
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeeDataPort
import org.breizhcamp.bilhed.domain.use_cases.ports.GroupPort
import org.breizhcamp.bilhed.domain.use_cases.ports.NotificationPort
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class PersonCrud(
    private val personPort: PersonPort,
    private val groupPort: GroupPort,
    private val attendeeDataPort: AttendeeDataPort,
    private val notificationPort: NotificationPort
) {
    fun filter(filter: PersonFilter): List<Person> = personPort.filter(filter)

    fun get(id: UUID): Person = personPort.get(id)

    fun updateContact(id: UUID, req: UpdateContactReq) {
        personPort.updateContact(id, req)
    }

    fun getPersonComplete(id: UUID): PersonComplete {
        return PersonComplete(
            person = personPort.get(id = id),
            group = groupPort.getBy(memberId = id),
            attendeeData = attendeeDataPort.getData(id = id),
            notifs = notificationPort.listBy(personId = id)
        )
    }
}