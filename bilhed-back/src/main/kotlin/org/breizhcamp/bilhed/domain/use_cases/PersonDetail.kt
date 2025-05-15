package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.application.dto.admin.AttendeeDataDTO
import org.breizhcamp.bilhed.application.dto.admin.PersonDTO
import org.breizhcamp.bilhed.application.dto.admin.UpdateContactReq
import org.breizhcamp.bilhed.application.rest.admin.toDTO
import org.breizhcamp.bilhed.application.rest.admin.toDto
import org.breizhcamp.bilhed.domain.entities.Attendee
import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class PersonDetail (
    private val personPort: PersonPort,
    private val attendeePort: AttendeePort
) {
    fun get(id: UUID): PersonDTO {
      val p = personPort.get(id)
        return when (p) {
            is Participant -> p.toDto()
            is Attendee -> {
                val data = attendeePort.getData(id)
                if (data == null) {
                    return p.toDto()
                }
                return AttendeeDataDTO(
                    p.toDto(),
                    data.company,
                    data.tShirtSize,
                    data.tShirtCut,
                    data.vegan,
                    data.meetAndGreet,
                    data.postalCode
                )
            }
            is Registered -> p.toDTO()
            else -> throw IllegalArgumentException("Unknown person type")
        }
    }

    fun updateContact(id: UUID, req: UpdateContactReq) {
        personPort.updateContact(id, req)
    }
}