package org.breizhcamp.bilhed.application.rest

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.application.dto.*
import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.PersonCrud
import org.breizhcamp.bilhed.domain.use_cases.ReferentInfosCrud
import org.breizhcamp.bilhed.domain.use_cases.Registration
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/register")
class RegisterCtrl(
    private val registration: Registration,
    private val personCrud: PersonCrud,
    private val referentInfosCrud: ReferentInfosCrud,
) {

    @PostMapping
    fun register(@RequestBody req: GroupRegisterReq): RegisterRes {
        req.validate(req.groupPayment)
        val referentId = UUID.randomUUID()
        val group = registration.registerGroup(req.toGroup(referentId))

        val members = listOf(req.referent.toPerson(group.id, referentId)) + req.companions.map { it.toPerson(group.id, req.referent.pass) }
        registration.registerMembers(referentId, members)

        return RegisterRes(referentId)
    }

    @GetMapping("/{id}")
    fun getRegisterState(@PathVariable id: UUID): RegisterStateRes {
        val pers = personCrud.get(id)
        val refInfos = referentInfosCrud.get(id)
        return RegisterStateRes(pers.localPhone(), refInfos.nbSmsSent)
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
    fun handleENFE() = ErrorRes("Une erreur est survenue")

    @ExceptionHandler(IllegalArgumentException::class) @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIAE(e: IllegalArgumentException) = ErrorRes(e.message ?: "Une erreur est survenue")

    private fun CompanionRegisterReq.toPerson(groupId: UUID, pass: PassType, id: UUID = UUID.randomUUID()) = Person(
        id,
        lastname.trim(),
        firstname.trim(),
        PersonStatus.REGISTERED,
        internationalPhone() ,
        email,
        pass,
        groupId
    )

    private fun ReferentRegisterReq.toPerson(groupId: UUID, id: UUID) = Person(
        id,
        lastname.trim(),
        firstname.trim(),
        PersonStatus.REGISTERED,
        internationalPhone(),
        email,
        pass,
        groupId
    )

    private fun GroupRegisterReq.toGroup(referentId: UUID, groupId: UUID = UUID.randomUUID()) = Group(groupId, referentId, groupPayment)
}
