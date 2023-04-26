package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.time.ZonedDateTime
import java.util.*

@Entity @Table(name = "participant")
data class ParticipantDB(
    @Id
    val id: UUID,

    @Enumerated(EnumType.STRING)
    var status: ParticipantDBStatus,

    val lastname: String,
    val firstname: String,
    val email: String,
    val telephone: String,

    val registrationDate: ZonedDateTime,
    var participationDate: ZonedDateTime? = null,

    @Enumerated(EnumType.STRING)
    val registrationSmsStatus: SmsStatus,
    val registrationNbSmsSent: Int,
    val registrationLastSmsSentDate: ZonedDateTime?,
    val registrationToken: String?,
)

enum class ParticipantDBStatus {
    REGISTERED, PARTICIPANT, ATTENDEE
}
