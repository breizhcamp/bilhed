package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.domain.entities.Attendee
import org.breizhcamp.bilhed.domain.entities.AttendeeData
import org.breizhcamp.bilhed.domain.entities.AttendeeFilter
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.infrastructure.db.model.AttendeeDataDB
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB
import org.breizhcamp.bilhed.infrastructure.db.repos.AttendeeDataRepo
import org.breizhcamp.bilhed.infrastructure.db.repos.ParticipantRepo
import org.springframework.stereotype.Component
import java.util.*

@Component
class AttendeeAdapter(
    private val attendeeDataRepo: AttendeeDataRepo,
    private val participantRepo: ParticipantRepo,
): AttendeePort {

    override fun filter(filter: AttendeeFilter): List<Attendee> {
        return participantRepo.filterAttendee(filter).map { it.toAttendee() }
    }

    override fun get(id: UUID): Attendee {
        return participantRepo.findAttendee(id)?.toAttendee() ?: throw EntityNotFoundException("Unable to find attendee [$id]")
    }

    override fun saveData(id: UUID, data: AttendeeData) {
        attendeeDataRepo.save(data.toDB(id))
    }

    override fun setPayed(ids: List<UUID>) {
        participantRepo.setPayed(ids)
    }

}

private fun AttendeeData.toDB(id: UUID) = AttendeeDataDB(
    id = id,
    company = company,
    tShirtSize = tShirtSize,
    tShirtCut = tShirtCut,
    vegan = vegan,
    meetAndGreet = meetAndGreet,
    postalCode = postalCode,
)

private fun ParticipantDB.toAttendee() = Attendee(
    id = id,
    lastname = lastname,
    firstname = firstname,
    email = email,
    telephone = telephone,
    pass = pass,
    kids = kids,

    confirmationLimitDate = requireNotNull(participantConfirmationLimitDate) { "Attendee [$id] has no confirmation limit date" },
    participantConfirmationDate = requireNotNull(participantConfirmationDate) { "Attendee [$id] has no confirmation date" },
    payed = payed,
)