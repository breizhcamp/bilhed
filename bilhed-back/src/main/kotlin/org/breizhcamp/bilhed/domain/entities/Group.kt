package org.breizhcamp.bilhed.domain.entities

import java.util.UUID

data class Group(
    val id: UUID,
    val referentId: UUID,
    val groupPayment: Boolean,
    val drawOrder: Int? = null
)
