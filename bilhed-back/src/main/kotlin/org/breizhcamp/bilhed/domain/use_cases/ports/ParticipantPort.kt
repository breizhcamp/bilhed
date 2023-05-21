package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Participant

interface ParticipantPort {

    fun list(): List<Participant>
}