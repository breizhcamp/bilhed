package org.breizhcamp.bilhed.domain.entities

data class ParticipantFilter(
    override val lastname: String?,
    override val firstname: String?,
    override val email: String?,
    override val pass: PassType?,
    val success: Boolean?,
): PersonFilter() {
    companion object {
        fun empty() = ParticipantFilter(null, null, null, null, null)
    }
}