package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.breizhcamp.bilhed.domain.entities.SmsStatus
import java.time.ZonedDateTime
import java.util.UUID

@Entity @Table(name = "registration_infos")
data class RegistrationInfosDB(

    @Id
    val personId: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "person_id")
    val person: PersonDB,

    val registrationDate: ZonedDateTime,

    @Enumerated(EnumType.STRING)
    var registrationSmsStatus: SmsStatus,

    var registrationNbSmsSent: Int,
    var registrationLastSmsSentDate: ZonedDateTime?,
    var registrationSmsError: String? = null,
    var registrationToken: String,
    var registrationNbTokenTries: Int = 0,
)
