package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.Attendee
import org.breizhcamp.bilhed.domain.entities.AttendeeData
import org.breizhcamp.bilhed.domain.entities.AttendeeFilter
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toAttendee
import org.breizhcamp.bilhed.infrastructure.db.mappers.toAttendeeData
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDBStatus
import org.breizhcamp.bilhed.infrastructure.db.repos.AttendeeDataRepo
import org.breizhcamp.bilhed.infrastructure.db.repos.ParticipantRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class AttendeeAdapter(
    private val attendeeDataRepo: AttendeeDataRepo,
    private val participantRepo: ParticipantRepo,
): AttendeePort {
    override fun list(): List<Attendee> = participantRepo.listAttendees().map { it.toAttendee() }

    override fun filter(filter: AttendeeFilter): List<Attendee> {
        return participantRepo.filterAttendee(filter).map { it.toAttendee() }
    }

    override fun get(id: UUID): Attendee {
        return participantRepo.findAttendee(id)?.toAttendee() ?: throw EntityNotFoundException("Unable to find attendee [$id]")
    }

    override fun get(ids: List<UUID>): List<Attendee> {
        return participantRepo.findAttendees(ids).map { it.toAttendee() }
    }

    override fun saveData(id: UUID, data: AttendeeData) {
        attendeeDataRepo.save(data.toDB(id))
    }

    override fun getData(id: UUID): AttendeeData? {
        return attendeeDataRepo.findByIdOrNull(id)?.let { return it.toAttendeeData() }
    }

    override fun setPayed(ids: List<UUID>) {
        participantRepo.setPayed(ids)
    }

    override fun listWithData(): List<Pair<Attendee, AttendeeData?>> {
        val attendees = participantRepo.listAttendees()
        val attendeesData = attendeeDataRepo.findAll()
        return attendees.map { attendee ->
            val attendeeData = attendeesData.find { it.id == attendee.id }
            attendee.toAttendee() to attendeeData?.toAttendeeData()
        }
    }

    override fun levelUpToReleased(id: UUID) {
        participantRepo.findAttendee(id)?.apply {
            status = ParticipantDBStatus.RELEASED
        }
    }

}
