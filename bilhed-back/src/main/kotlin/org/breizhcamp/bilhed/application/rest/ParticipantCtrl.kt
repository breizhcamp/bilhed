package org.breizhcamp.bilhed.application.rest

import jakarta.persistence.EntityNotFoundException
import mu.KotlinLogging
import org.breizhcamp.bilhed.application.dto.*
import org.breizhcamp.bilhed.domain.entities.AttendeeData
import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.ParticipantConfirmInfo
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
    fun get(@PathVariable id: UUID): ParticipantConfirmInfoRes {
        return participantConfirm.get(id).toDTO()
    }

    @PostMapping("/group/confirm")
    fun confirm(@RequestBody req: List<ParticipantConfirmReq>): ConfirmRes {
        req.forEach { it.validate() }
//        logger.info { "Confirm participant [$id] with req: $req" }
        return participantConfirm.confirm(req.map { it.id to it.toData() }).toConfirmRes()
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

private fun ParticipantConfirmInfo.toDTO() = ParticipantConfirmInfoRes(
    groupPayment = groupPayment,
    referent = referent.toDTO(),
    companions = companions.map { it.toDTO() },
    confirmationLimitDate = confirmationLimitDate
)

private fun Person.toDTO() = PersonDTO(
    id = id,
    lastname = lastname,
    firstname = firstname,
    status = status,
    telephone = telephone,
    email = email,
    pass = pass,
    kids = kids,
    groupId = groupId,
    payed = payed
)

private fun Ticket.toConfirmRes() = ConfirmRes(payUrl, payed)
