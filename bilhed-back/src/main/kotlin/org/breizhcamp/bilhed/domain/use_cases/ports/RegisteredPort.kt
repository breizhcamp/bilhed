package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Registered
import java.util.*

interface RegisteredPort {

    fun list(): List<Registered>

    fun existsEmailOrPhone(email: String, phone: String): Boolean

    fun existPhone(phone: String): Boolean

    fun save(registered: Registered)

    fun get(id: UUID): Registered

    /** Confirmed the registered to be a participant to the lottery */
    fun levelUpToParticipant(id: UUID)

}