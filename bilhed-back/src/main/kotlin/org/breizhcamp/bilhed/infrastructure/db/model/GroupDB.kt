package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.breizhcamp.bilhed.domain.entities.PassType
import java.util.*

@Entity @Table(name = "person_group")
data class GroupDB(
    @Id
    val id: UUID,

    val referentId: UUID,

    @Enumerated(EnumType.STRING)
    val pass: PassType,

    val groupPayment: Boolean,
    val drawOrder: Int? = null
)
