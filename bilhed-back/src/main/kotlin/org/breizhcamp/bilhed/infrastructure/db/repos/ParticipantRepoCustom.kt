package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.domain.entities.ParticipantFilter
import org.breizhcamp.bilhed.infrastructure.db.model.ParticipantDB

interface ParticipantRepoCustom {

    fun filter(filter: ParticipantFilter): List<ParticipantDB>

}