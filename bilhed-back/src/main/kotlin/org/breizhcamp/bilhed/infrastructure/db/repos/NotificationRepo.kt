package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.infrastructure.db.model.NotificationDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NotificationRepo: JpaRepository<NotificationDB, UUID> {

    fun findByPersonId(personId: UUID): List<NotificationDB>
  
    @Query("select distinct on (person_id) * from notification where person_id in (:personIds) order by person_id, reminder_date DESC", nativeQuery = true)
    fun findLatestReminderPerPerson(personIds: List<UUID>): List<NotificationDB>

}