package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.infrastructure.db.model.GroupDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GroupRepo: JpaRepository<GroupDB, UUID> {

    fun countByReferentId(referentId: UUID): Int

    fun findByReferentId(referentId: UUID): GroupDB?

    @Query("SELECT g FROM GroupDB g WHERE g.id in (:ids)")
    fun findGroups(ids: List<UUID>): List<GroupDB>
}