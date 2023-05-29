package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.entities.PassType
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface ParticipantPort {

    fun list(): List<Participant>

    fun get(id: UUID): Participant

    fun save(participant: Participant)

    fun listTopDrawByPassWithLimit(pass: PassType, limit: Int): List<Participant>

    fun listIdsWithNoDraw(): Map<PassType, List<UUID>>

    fun updateDrawOrder(id: UUID, drawOrder: Int)

    fun getAlreadyNotifCount(): Map<PassType, Int>
}