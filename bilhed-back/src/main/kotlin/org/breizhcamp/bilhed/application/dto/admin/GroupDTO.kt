package org.breizhcamp.bilhed.application.dto.admin

import org.breizhcamp.bilhed.domain.entities.PassType
import java.util.UUID

data class GroupDTO(
    val id: UUID,
    val referentId: UUID,
    val pass: PassType,
    val groupPayment: Boolean,
    val drawOrder: Int? = null
)
