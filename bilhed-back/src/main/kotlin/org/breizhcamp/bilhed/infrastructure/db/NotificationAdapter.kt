package org.breizhcamp.bilhed.infrastructure.db

import org.breizhcamp.bilhed.domain.entities.Notification
import org.breizhcamp.bilhed.domain.use_cases.ports.NotificationPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toDB
import org.breizhcamp.bilhed.infrastructure.db.mappers.toNotification
import org.breizhcamp.bilhed.infrastructure.db.repos.NotificationRepo
import org.springframework.stereotype.Component
import java.util.*

@Component
class NotificationAdapter (
    val notificationRepo: NotificationRepo,
): NotificationPort {
    override fun save(notification: Notification) {
        notificationRepo.save(notification.toDB())
    }

    override fun listByPersonId(personId: UUID): List<Notification> {
        return notificationRepo.findByPersonId(personId).map { it.toNotification() }
    }

    override fun findLatestReminderPerPerson(personIds: List<UUID>): List<Notification> {
        return notificationRepo.findLatestReminderPerPerson(personIds).map { it.toNotification() }
    }


}