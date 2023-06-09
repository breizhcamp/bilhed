package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.domain.entities.AttendeeFilter
import org.breizhcamp.bilhed.domain.entities.ParticipantFilter
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB

interface ParticipantRepoCustom {

    fun filterParticipant(filter: ParticipantFilter): List<ParticipantDB>

    fun filterAttendee(filter: AttendeeFilter): List<ParticipantDB>

}