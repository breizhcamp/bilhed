package org.breizhcamp.bilhed.application.rest

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.application.dto.*
import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.Registration
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/register")
class RegisterCtrl(
    private val registration: Registration,
) {

    @PostMapping
    fun register(@RequestBody req: GroupRegisterReq): RegisterRes {
        // TODO : quoi retourner ? av : id de l'inscrit
        req.validate(req.groupPayment)
        val groupId = UUID.randomUUID()

        val members = mutableListOf(req.referent.toPerson(groupId))
        members.addAll(req.companions.map { it.toPerson(groupId, members.first().pass) })
        val referent = registration.registerMembers(members)

        val group = registration.registerGroup(req.toGroup(referent.id, groupId))
        return RegisterRes(group.id)
    }

    @GetMapping("/{id}")
    fun getRegisterState(@PathVariable id: UUID): RegisterStateRes {
        return registration.getReferent(id).toStateRes()
    }

    @PostMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun validateToken(@PathVariable id: UUID, @RequestBody tokenReq: TokenValidReq) {
        registration.validateToken(id, tokenReq.code)
    }

    @PutMapping("/{id}/phone") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changePhoneNumber(@PathVariable id: UUID, @RequestBody req: ChangePhoneReq) {
        registration.changePhoneNumber(id, req.internationalPhone())
    }

    @PostMapping("/{id}/resend-sms") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun resendSMS(@PathVariable id: UUID) {
        registration.resendSms(id)
    }

    @ExceptionHandler(EntityNotFoundException::class) @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleENFE(e: EntityNotFoundException) = ErrorRes("Une erreur est survenue")

    @ExceptionHandler(IllegalArgumentException::class) @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIAE(e: IllegalArgumentException) = ErrorRes(e.message ?: "Une erreur est survenue")

    private fun CompanionRegisterReq.toPerson(groupId: UUID, pass: PassType, id: UUID = UUID.randomUUID()) = Person(
        id,
        lastname.trim(),
        firstname.trim(),
        PersonStatus.REGISTERED,
        telephone,
        email,
        pass,
        kids,
        groupId
    )

    private fun ReferentRegisterReq.toPerson(groupId: UUID, id: UUID = UUID.randomUUID()) = Person(
        id,
        lastname.trim(),
        firstname.trim(),
        PersonStatus.REGISTERED,
        internationalPhone(),
        email,
        pass,
        kids,
        groupId
    )


    private fun Referent.toStateRes() = RegisterStateRes(person.localPhone(), referentInfos.nbSmsSent)

    private fun GroupRegisterReq.toGroup(referentId: UUID, groupId: UUID) = Group(groupId, referentId, groupPayment)
}
