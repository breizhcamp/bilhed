package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.time.ZonedDateTime
import java.util.UUID

@Entity @Table(name = "participation_infos")
data class ParticipationInfosDB(

    @Id
    val personId: UUID,

    @Enumerated(EnumType.STRING)
    var participantSmsStatus: SmsStatus? = null,
    var participantNbSmsSent: Int = 0,
    var participantSmsError: String? = null,
    var participantNotificationConfirmSentDate: ZonedDateTime? = null,

    var participantConfirmationDate: ZonedDateTime? = null,
)
