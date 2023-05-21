package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.breizhcamp.bilhed.domain.entities.ConfirmationType
import org.breizhcamp.bilhed.domain.entities.PassType
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

    @Enumerated(EnumType.STRING)
    val pass: PassType,

    val kids: String?,

    val registrationDate: ZonedDateTime,
    var participationDate: ZonedDateTime? = null,

    @Enumerated(EnumType.STRING)
    val registrationSmsStatus: SmsStatus,
    val registrationNbSmsSent: Int,
    val registrationLastSmsSentDate: ZonedDateTime?,
    val registrationSmsError: String? = null,
    val registrationToken: String,
    val registrationNbTokenTries: Int = 0,


    val drawOrder: Int? = null,
    @Enumerated(EnumType.STRING)
    val participantSmsStatus: SmsStatus? = null,
    val participantNbSmsSent: Int = 0,
    val participantSmsError: String? = null,
    val participantSmsConfirmSentDate: ZonedDateTime? = null,
    val participantMailConfirmSentDate: ZonedDateTime? = null,

    val participantConfirmationDate: ZonedDateTime? = null,
    @Enumerated(EnumType.STRING)
    val participantConfirmationType: ConfirmationType? = null,
)

enum class ParticipantDBStatus {
    REGISTERED, PARTICIPANT, ATTENDEE
}
