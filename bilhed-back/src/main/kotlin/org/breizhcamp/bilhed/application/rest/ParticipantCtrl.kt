package org.breizhcamp.bilhed.application.rest

import jakarta.persistence.EntityNotFoundException
import mu.KotlinLogging
import org.breizhcamp.bilhed.application.dto.ErrorRes
import org.breizhcamp.bilhed.application.dto.ParticipantConfirmReq
import org.breizhcamp.bilhed.application.dto.ParticipantConfirmInfo
import org.breizhcamp.bilhed.application.dto.ConfirmRes
import org.breizhcamp.bilhed.domain.entities.*
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
    fun confirm(@PathVariable id: UUID, @RequestBody req: ParticipantConfirmReq): ConfirmRes {
        req.validate()
        logger.info { "Confirm participant [$id] with req: $req" }
        return participantConfirm.confirm(id, req.toData()).toConfirmRes()
    }

    @PostMapping("/{id}/cancel")
    fun cancel(@PathVariable id: UUID) {
        logger.info { "Cancel participant [$id]" }
        participantConfirm.cancel(id)
    }

    @ExceptionHandler(IllegalArgumentException::class) @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIAE(e: IllegalArgumentException) = ErrorRes(e.message ?: "Une erreur est survenue")

    @ExceptionHandler(EntityNotFoundException::class) @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleENFE(e: EntityNotFoundException) = ErrorRes("Not found")
}

private fun ParticipantConfirmReq.toData() = AttendeeData(
    company = company,
    tShirtSize = tShirtSize,
    tShirtCut = tShirtCut,
    vegan = vegan ?: false,
    meetAndGreet = meetAndGreet,
    postalCode = postalCode,
)

private fun Person.toDTO() = ParticipantConfirmInfo(
    lastname = lastname,
    firstname = firstname,
    email = email,
    pass = pass,
    confirmationLimitDate = when(this) {
        is Participant -> requireNotNull(notificationConfirmDate)
        is Attendee -> participantNotificationConfirmDate
        else -> throw IllegalStateException("Not a participant")
    },
)

private fun Ticket.toConfirmRes() = ConfirmRes(payUrl, payed)
