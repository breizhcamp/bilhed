package org.breizhcamp.bilhed.domain.entities

data class Mail(
    val to: List<MailAddress>,
    val template: String,
    val model: Map<String, String>,
)

data class MailAddress(
    val email: String,
    val name: String? = null,
)
