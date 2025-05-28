package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.infrastructure.db.model.ReminderConfigDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ReminderConfigRepo: JpaRepository<ReminderConfigDB, UUID> {
    @Query("select count(bc) from ReminderConfigDB bc where bc.type = :type")
    fun countByType(type: String): Int

    fun findByType(type: String): List<ReminderConfigDB>
}