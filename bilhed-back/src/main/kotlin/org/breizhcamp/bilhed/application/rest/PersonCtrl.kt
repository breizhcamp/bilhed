package org.breizhcamp.bilhed.application.rest

import org.breizhcamp.bilhed.application.dto.EndInfoDTO
import org.breizhcamp.bilhed.application.dto.PersonDataTicketDTO
import org.breizhcamp.bilhed.domain.entities.EndInfo
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

    @GetMapping("/{id}/end")
    fun getEndInfos(@PathVariable id: UUID): EndInfoDTO {
        return personDataTicketInfo.getEndInfos(id).toDto()
    }
}

private fun PersonDataTicket.toDto() = PersonDataTicketDTO(
    hasAttendeeData = hasAttendeeData,
    hasTicket = hasTicket,
    hasPayed = hasPayed,
    payUrl = payUrl,
)

private fun EndInfo.toDto() = EndInfoDTO(
    groupPayment = groupPayment,
    nbMembers = nbMembers,
)
