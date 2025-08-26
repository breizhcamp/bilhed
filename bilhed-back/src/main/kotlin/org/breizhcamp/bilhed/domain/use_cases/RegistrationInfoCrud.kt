package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.RegistrationInfo
import org.breizhcamp.bilhed.domain.use_cases.ports.RegistrationInfoPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class RegistrationInfoCrud(
    val registrationInfoPort: RegistrationInfoPort
) {
    fun get(id: UUID): RegistrationInfo = registrationInfoPort.get(id)

    fun get(ids: List<UUID>): List<RegistrationInfo> = registrationInfoPort.get(ids)
}