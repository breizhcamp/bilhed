package org.breizhcamp.bilhed.mail.domain.entities

data class Mail(
    val from: MailAddress,
    val to: List<MailAddress>,
    val cc: List<MailAddress> = emptyList(),
    val bcc: List<MailAddress> = emptyList(),
    val template: String,
    val model: Map<String, String>,
)

data class MailAddress(
    val email: String,
    val name: String? = null
)