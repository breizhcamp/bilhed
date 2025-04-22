package org.breizhcamp.bilhed.domain.entities

import java.util.UUID

data class Mail(
    val to: List<MailAddress>,
    val template: String,
    val model: Map<String, String>,
    val personIds: List<UUID>
)

data class MailAddress(
    val email: String,
    val name: String? = null,
)
