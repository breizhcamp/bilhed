package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.ZonedDateTime
import java.util.UUID

@Entity @Table(name = "notification")
data class NotificationDB (

    @Id
    val id: UUID,
    val date: ZonedDateTime,
    val template: String,

    @Enumerated(EnumType.STRING)
    val method: NotifDBMethod,

    @Column(name="person_id")
    val personId: UUID,
    val model: String,

    @Enumerated(EnumType.STRING)
    val origin: NotifDBOrigin
)

enum class NotifDBMethod {
    SMS, MAIL
}

enum class NotifDBOrigin {
    AUTOMATIC, MANUAL
}