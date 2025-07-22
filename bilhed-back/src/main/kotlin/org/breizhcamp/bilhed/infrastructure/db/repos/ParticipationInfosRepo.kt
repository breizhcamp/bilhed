package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.infrastructure.db.model.ParticipationInfosDB
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDBStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ParticipationInfosRepo: JpaRepository<ParticipationInfosDB, UUID> {

    @Query("SELECT pi FROM ParticipationInfosDB pi WHERE pi.person.group.id = :groupId")
    fun findByGroupId(groupId: UUID): List<ParticipationInfosDB>

    @Query("SELECT pi FROM ParticipationInfosDB pi WHERE pi.person.group.id IN (:groupIds)")
    fun findAllByGroupIds(groupIds: List<UUID>): List<ParticipationInfosDB>

    @Query("SELECT pi FROM ParticipationInfosDB pi WHERE pi.person.status = :status")
    fun findAllByStatus(status: PersonDBStatus): List<ParticipationInfosDB>
}