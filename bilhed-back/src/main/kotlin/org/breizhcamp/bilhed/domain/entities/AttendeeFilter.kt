package org.breizhcamp.bilhed.domain.entities

data class AttendeeFilter(
    override val lastname: String?,
    override val firstname: String?,
    override val email: String?,
    override val pass: PassType?,
    val payed: Boolean?,
): PersonFilter() {
    companion object {
        fun empty() = AttendeeFilter(null, null, null, null, null)
    }
}