package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.ParticipantFilter
import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipantPort
import org.springframework.stereotype.Service

@Service
class ParticipantList(
    val participantPort: ParticipantPort,
) {

    fun list(): List<Participant> = participantPort.list()

    fun filter(filter: ParticipantFilter): List<Participant> = participantPort.filter(filter)

}