package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.*
import java.util.*

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

    val payed: Boolean = false,

    @OneToOne(fetch = FetchType.LAZY)
    val group: GroupDB
)

enum class PersonDBStatus {
    REGISTERED, PARTICIPANT, ATTENDEE, RELEASED, BLOCKED
}
