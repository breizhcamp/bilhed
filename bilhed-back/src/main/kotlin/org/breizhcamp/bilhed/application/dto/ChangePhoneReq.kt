package org.breizhcamp.bilhed.application.dto

data class ChangePhoneReq(
    val phone: String,
) {
    fun internationalPhone(): String {
        return if (phone.startsWith("0")) "+33${phone.substring(1)}" else phone
    }
}
