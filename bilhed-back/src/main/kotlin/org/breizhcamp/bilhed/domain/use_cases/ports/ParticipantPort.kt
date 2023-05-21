package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.entities.PassType
import java.util.UUID

interface ParticipantPort {

    fun list(): List<Participant>

    fun listIdsWithNoDraw(): Map<PassType, List<UUID>>

    fun updateDrawOrder(id: UUID, drawOrder: Int)
}