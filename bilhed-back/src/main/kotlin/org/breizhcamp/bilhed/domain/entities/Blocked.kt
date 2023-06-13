package org.breizhcamp.bilhed.domain.entities

import java.util.*

data class Blocked(
    override val id: UUID,
    override val lastname: String,
    override val firstname: String,
    override val email: String,
    override val telephone: String,
    override val pass: PassType,
    override val kids: String?,
): Person()
