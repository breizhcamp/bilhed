package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.Notification
import org.breizhcamp.bilhed.domain.use_cases.ports.NotificationPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NotificationCrud (
    private val notificationPort : NotificationPort
) {
    fun listByPersonId(id: UUID): List<Notification> {
        return notificationPort.listBy(id)
    }
}