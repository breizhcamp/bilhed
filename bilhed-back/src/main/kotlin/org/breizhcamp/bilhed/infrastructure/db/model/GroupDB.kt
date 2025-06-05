package org.breizhcamp.bilhed.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity @Table(name = "person_group")
data class GroupDB(
    @Id
    val id: UUID,

    val referentId: UUID,
    val groupPayment: Boolean,
    val drawOrder: Int? = null
)
