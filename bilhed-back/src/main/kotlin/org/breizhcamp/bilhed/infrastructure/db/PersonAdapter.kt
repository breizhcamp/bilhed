package org.breizhcamp.bilhed.infrastructure.db

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.application.dto.admin.UpdateContactReq
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.*
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDBStatus
import org.breizhcamp.bilhed.infrastructure.db.repos.ParticipantRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class PersonAdapter(
    private val participantRepo: ParticipantRepo,
): PersonPort {

    override fun get(id: UUID): Person {
        val p = participantRepo.findByIdOrNull(id) ?: throw EntityNotFoundException("Unable to find person [$id]")
        return when (p.status) {
            ParticipantDBStatus.REGISTERED -> p.toRegistered()
            ParticipantDBStatus.PARTICIPANT -> p.toParticipant()
            ParticipantDBStatus.ATTENDEE -> p.toAttendee()
            ParticipantDBStatus.RELEASED -> p.toReleased()
            ParticipantDBStatus.BLOCKED -> p.toBlocked()
        }
    }

    @Transactional
    override fun updateContact(id: UUID, updateContactReq: UpdateContactReq) {
        participantRepo.findByIdOrNull(id) ?: throw EntityNotFoundException("Unable to find person [$id]")
        participantRepo.updateContact(id, updateContactReq.telephone, updateContactReq.email)
    }

}