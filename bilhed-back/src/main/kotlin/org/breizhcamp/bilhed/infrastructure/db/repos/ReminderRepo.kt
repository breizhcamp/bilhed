package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.infrastructure.db.model.ReminderDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ReminderRepo: JpaRepository<ReminderDB, UUID> {

    fun findByPersonId(personId: UUID): List<ReminderDB>

}