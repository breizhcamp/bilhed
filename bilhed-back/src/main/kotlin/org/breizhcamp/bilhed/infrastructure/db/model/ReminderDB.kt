package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.ZonedDateTime
import java.util.UUID

@Entity @Table(name = "reminder")
data class ReminderDB (

    @Id
    val id: UUID,
    val reminderDate: ZonedDateTime,
    val template: String,

    @Enumerated(EnumType.STRING)
    val method: ReminderDBMethod,

    val participantId: UUID,
)

enum class ReminderDBMethod {
    SMS, MAIL
}