package org.breizhcamp.bilhed.application.rest.admin

import org.breizhcamp.bilhed.application.dto.admin.ParticipantDTO
import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.use_cases.ParticipantList
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/participants")
class ParticipantCtrl(
    private val participantList: ParticipantList,
) {

    @GetMapping
    fun listParticipants(): List<ParticipantDTO> = participantList.list().map { it.toDto() }

}

private fun Participant.toDto() = ParticipantDTO(
    id = id,
    lastname = lastname,
    firstname = firstname,
    email = email,
    telephone = telephone,
    pass = pass,
    kids = kids,

    drawOrder = drawOrder,
    smsStatus = smsStatus,
    nbSmsSent = nbSmsSent,
    smsError = smsError,
    smsConfirmSentDate = smsConfirmSentDate,
    mailConfirmSentDate = mailConfirmSentDate,
    confirmationDate = confirmationDate,
    confirmationType = confirmationType,
)
