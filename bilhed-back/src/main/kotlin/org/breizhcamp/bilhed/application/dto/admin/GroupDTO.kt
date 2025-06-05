package org.breizhcamp.bilhed.application.dto.admin

import java.util.UUID

data class GroupDTO(
    val id: UUID,
    val referentId: UUID,
    val groupPayment: Boolean,
    val drawOrder: Int? = null
)
