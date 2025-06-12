package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.*
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.time.ZonedDateTime
import java.util.UUID

@Entity @Table(name = "participation_infos")
data class ParticipationInfosDB(

    @Id
    val personId: UUID,

    @OneToOne
    @MapsId
    @JoinColumn(name = "person_id")
    val person: PersonDB,

    @Enumerated(EnumType.STRING)
    var participantSmsStatus: SmsStatus? = null,
    var participantNbSmsSent: Int = 0,
    var participantSmsError: String? = null,
    var participantNotificationConfirmSentDate: ZonedDateTime? = null,

    var participantConfirmationDate: ZonedDateTime? = null,
)
