package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity @Table(name = "attendee_data")
data class AttendeeDataDB(
    @Id
    val id: UUID,
    val company: String?,
    val tShirtSize: String,
    val tShirtCut: String?,
    val vegan: Boolean,
    val meetAndGreet: Boolean,
    val postalCode: String?,
)
