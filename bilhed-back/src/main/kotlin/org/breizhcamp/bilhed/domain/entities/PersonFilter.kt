package org.breizhcamp.bilhed.domain.entities

sealed class PersonFilter {
    abstract val lastname: String?
    abstract val firstname: String?
    abstract val email: String?
    abstract val pass: PassType?
}