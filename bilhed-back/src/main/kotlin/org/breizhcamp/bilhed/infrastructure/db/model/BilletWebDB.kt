package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity @Table(name = "billetweb")
data class BilletWebDB(
    @Id
    val participantId: UUID,
    val attendeeId: String,
    val orderManagerUrl: String,
)
