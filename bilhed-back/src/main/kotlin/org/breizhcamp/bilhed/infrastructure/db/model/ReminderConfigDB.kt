package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity @Table(name = "reminder_config")
data class ReminderConfigDB (

    @Id
    val id: UUID,
    val type: String, // inscription, tirage au sort, paiement
    val hours: Int,
    val templateMail: String,
    val templateSms: String
)