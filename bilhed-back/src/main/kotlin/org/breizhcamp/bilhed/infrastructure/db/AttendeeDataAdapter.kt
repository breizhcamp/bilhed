package org.breizhcamp.bilhed.infrastructure.db

import org.breizhcamp.bilhed.domain.entities.AttendeeData
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeeDataPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toAttendeeData
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.mappers.toPerson
import org.breizhcamp.bilhed.infrastructure.db.repos.AttendeeDataRepo
import org.breizhcamp.bilhed.infrastructure.db.repos.PersonRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class AttendeeDataAdapter(
    private val attendeeDataRepo: AttendeeDataRepo,
    private val personRepo: PersonRepo,
): AttendeeDataPort {

    override fun saveData(id: UUID, data: AttendeeData) {
        attendeeDataRepo.save(data.toDB(id))
    }

    override fun getData(id: UUID): AttendeeData? {
        return attendeeDataRepo.findByIdOrNull(id)?.let { return it.toAttendeeData() }
    }

    override fun listWithData(): List<Pair<Person, AttendeeData?>> {
        val attendees = personRepo.listAttendees()
        val attendeesData = attendeeDataRepo.findAll()
        return attendees.map { attendee ->
            val attendeeData = attendeesData.find { it.id == attendee.id }
            attendee.toPerson() to attendeeData?.toAttendeeData()
        }
    }

}
