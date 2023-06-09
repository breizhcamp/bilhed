package org.breizhcamp.bilhed.application.rest

import org.breizhcamp.bilhed.application.dto.PersonDataTicketDTO
import org.breizhcamp.bilhed.domain.entities.PersonDataTicket
import org.breizhcamp.bilhed.domain.use_cases.PersonDataTicketInfo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/persons")
class PersonCtrl(
    private val personDataTicketInfo: PersonDataTicketInfo,
) {

    @GetMapping("/{id}/ticket")
    fun getDataTicket(@PathVariable id: UUID): ResponseEntity<PersonDataTicketDTO> {
        return personDataTicketInfo.getInfos(id)?.let { ResponseEntity.ok(it.toDto()) }
            ?: ResponseEntity.noContent().build()
    }
}

private fun PersonDataTicket.toDto() = PersonDataTicketDTO(
    hasAttendeeData = hasAttendeeData,
    hasTicket = hasTicket,
    hasPayed = hasPayed,
    payUrl = payUrl,
)
