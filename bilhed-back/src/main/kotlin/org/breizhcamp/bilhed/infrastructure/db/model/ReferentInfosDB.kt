package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.time.ZonedDateTime
import java.util.UUID

@Entity @Table(name = "referent_infos")
data class ReferentInfosDB(

    @Id
    val personId: UUID,
    val registrationDate: ZonedDateTime,

    @Enumerated(EnumType.STRING)
    var registrationSmsStatus: SmsStatus,

    var registrationNbSmsSent: Int,
    var registrationLastSmsSentDate: ZonedDateTime?,
    var registrationSmsError: String? = null,
    var registrationToken: String,
    var registrationNbTokenTries: Int = 0,
)
