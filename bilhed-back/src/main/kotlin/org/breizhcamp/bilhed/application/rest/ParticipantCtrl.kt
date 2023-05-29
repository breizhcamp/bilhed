package org.breizhcamp.bilhed.application.rest

import mu.KotlinLogging
import org.breizhcamp.bilhed.application.dto.ParticipantConfirmReq
import org.breizhcamp.bilhed.application.dto.ParticipantConfirmRes
import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.use_cases.ParticipantConfirm
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/participants")
class ParticipantCtrl(
    private val participantConfirm: ParticipantConfirm,
) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): ParticipantConfirmRes {
        return participantConfirm.get(id).toDTO()
    }

    @PatchMapping("/{id}")
    fun confirm(@PathVariable id: UUID, @RequestBody req: ParticipantConfirmReq) {
        logger.info { "Confirm participant [$id] with coming [${req.coming}]" }
        //TODO plug saving data
    }
}

private fun Participant.toDTO() = ParticipantConfirmRes(
    firstname = firstname,
    confirmationLimitDate = requireNotNull(confirmationLimitDate),
)
