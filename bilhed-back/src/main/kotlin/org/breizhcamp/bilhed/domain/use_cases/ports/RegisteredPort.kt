package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Registered

interface RegisteredPort {

    fun existsEmailOrPhone(email: String, phone: String): Boolean

    fun save(registered: Registered)

}