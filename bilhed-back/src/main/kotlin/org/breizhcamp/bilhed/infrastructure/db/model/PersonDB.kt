package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.breizhcamp.bilhed.domain.entities.PassType
import java.util.UUID

@Entity @Table(name = "person")
data class PersonDB(
    @Id
    val id: UUID,

    @Enumerated(EnumType.STRING)
    var status: PersonDBStatus,

    val lastname: String,
    val firstname: String,
    val email: String,
    val telephone: String?,

    @Enumerated(EnumType.STRING)
    val pass: PassType,

    val payed: Boolean = false,

    @OneToOne(fetch = FetchType.LAZY)
    val group: GroupDB
)

enum class PersonDBStatus {
    REGISTERED, PARTICIPANT, ATTENDEE, RELEASED, BLOCKED
}
