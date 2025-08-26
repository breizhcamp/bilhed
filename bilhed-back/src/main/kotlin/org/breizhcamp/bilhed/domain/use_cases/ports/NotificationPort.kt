package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Notification
import java.util.UUID

interface NotificationPort {

    fun save(notification: Notification)

    fun listBy(personId: UUID): List<Notification>

    fun findLatestReminderPerPerson(personIds: List<UUID>): List<Notification>
}