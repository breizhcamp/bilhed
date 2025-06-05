package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.application.dto.admin.UpdateEmailReq
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class PersonDetail (
    private val personPort: PersonPort,
) {

    fun updateEmail(id: UUID, req: UpdateEmailReq) {
        personPort.updateEmail(id, req)
    }
}