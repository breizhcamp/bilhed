package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.infrastructure.db.model.GroupDB
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GroupRepo: JpaRepository<GroupDB, UUID> {

    fun countByReferentId(referentId: UUID): Int

    @Query("SELECT g FROM GroupDB g WHERE g.id IN (:ids)")
    fun findGroups(ids: List<UUID>): List<GroupDB>

    @Query("SELECT p FROM PersonDB p WHERE p.status = org.breizhcamp.bilhed.infrastructure.db.model.PersonDBStatus.PARTICIPANT AND p.group.drawOrder IS NULL")
    fun listGroupWithNoDraw(): List<PersonDB>

    @Modifying
    @Query("UPDATE GroupDB g SET g.drawOrder = :drawOrder WHERE g.id = :id")
    fun updateDrawOrder(id: UUID, drawOrder: Int)
}