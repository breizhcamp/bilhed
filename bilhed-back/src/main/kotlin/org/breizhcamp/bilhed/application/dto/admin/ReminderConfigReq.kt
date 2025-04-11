package org.breizhcamp.bilhed.application.dto.admin

data class ReminderConfigReq (
    val type: String,
    val hours: Int,
    val templateMail: String,
    val templateSms: String,
) {
    fun validate() {
        if (type.isBlank()) throw IllegalArgumentException("Le type ne peut pas être vide")
        if (templateSms.isBlank() && templateMail.isBlank()) throw IllegalArgumentException("Au moins une des template doit être renseignée")
    }
}