package org.breizhcamp.bilhed.application.rest

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.application.dto.*
import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.use_cases.Registration
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.text.Typography.registered

@RestController
@RequestMapping("/api/register")
class RegisterCtrl(
    private val registration: Registration,
) {

    @PostMapping
    fun register(@RequestBody req: RegisterReq): RegisterRes {
        req.validate()
        val saved = registration.register(req.toRegistered())
        return RegisterRes(saved.id)
    }

    @GetMapping("/{id}")
    fun getRegisterState(@PathVariable id: UUID): RegisterStateRes {
        return registration.get(id).toStateRes()
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

    private fun RegisterReq.toRegistered(id: UUID = UUID.randomUUID()) = Registered(id, lastname, firstname, email, internationalPhone(), pass, kids)
    private fun Registered.toStateRes() = RegisterStateRes(localPhone(), nbSmsSent)
}
