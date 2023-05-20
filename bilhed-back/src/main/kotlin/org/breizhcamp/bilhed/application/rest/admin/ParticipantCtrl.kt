package org.breizhcamp.bilhed.application.rest.admin

import org.breizhcamp.bilhed.application.dto.admin.ParticipantDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/participants")
class ParticipantCtrl {

    @GetMapping
    fun listParticipants(): List<ParticipantDTO> {
        return listOf(ParticipantDTO("id", "lastname", "firstname", "email", "telephone", "pass", "kids"))
    }

}