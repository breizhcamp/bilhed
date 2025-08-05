package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.RegistrationInfos
import org.breizhcamp.bilhed.domain.use_cases.ports.RegistrationInfosPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class RegistrationInfosCrud(
    val registrationInfosPort: RegistrationInfosPort
) {
    fun get(id: UUID): RegistrationInfos = registrationInfosPort.get(id)

    fun get(ids: List<UUID>): List<RegistrationInfos> = registrationInfosPort.get(ids)
}