package org.breizhcamp.bilhed.application.rest

import mu.KotlinLogging
import org.breizhcamp.bilhed.application.dto.ErrorRes
import org.breizhcamp.bilhed.application.dto.ParticipantConfirmReq
import org.breizhcamp.bilhed.application.dto.ParticipantConfirmInfo
import org.breizhcamp.bilhed.application.dto.ParticipantConfirmRes
import org.breizhcamp.bilhed.domain.entities.Participant
import org.breizhcamp.bilhed.domain.entities.Ticket
import org.breizhcamp.bilhed.domain.use_cases.ParticipantConfirm
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/participants")
class ParticipantCtrl(
    private val participantConfirm: ParticipantConfirm,
) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): ParticipantConfirmInfo {
        return participantConfirm.get(id).toDTO()
    }

    @PostMapping("/{id}/confirm")
    fun confirm(@PathVariable id: UUID, @RequestBody req: ParticipantConfirmReq): ParticipantConfirmRes {
        logger.info { "Confirm participant [$id]" }
        return participantConfirm.confirm(id).toConfirmRes()
    }

    @ExceptionHandler(IllegalArgumentException::class) @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIAE(e: IllegalArgumentException) = ErrorRes(e.message ?: "Une erreur est survenue")
}

private fun Participant.toDTO() = ParticipantConfirmInfo(
    firstname = firstname,
    confirmationLimitDate = requireNotNull(confirmationLimitDate),
)

private fun Ticket.toConfirmRes() = ParticipantConfirmRes(payUrl)
