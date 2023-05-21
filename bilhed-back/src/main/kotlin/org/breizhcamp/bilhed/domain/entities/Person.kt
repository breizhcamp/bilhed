package org.breizhcamp.bilhed.domain.entities

import java.util.*

sealed class Person {
    abstract val id: UUID

    abstract val lastname: String
    abstract val firstname: String
    abstract val email: String
    abstract val telephone: String
    abstract val pass: PassType
    abstract val kids: String?

    fun getMailAddress() = listOf(MailAddress(email, "$firstname $lastname"))

    fun localPhone(): String = if (telephone.startsWith("+33")) "0${telephone.substring(3)}" else telephone
}


