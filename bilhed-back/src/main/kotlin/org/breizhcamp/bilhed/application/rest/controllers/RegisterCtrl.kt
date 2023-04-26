package org.breizhcamp.bilhed.application.rest.controllers

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.application.rest.dto.RegisterStateRes
import org.breizhcamp.bilhed.application.rest.dto.RegisterReq
import org.breizhcamp.bilhed.application.rest.dto.RegisterRes
import org.breizhcamp.bilhed.application.rest.dto.TokenValidReq
import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.use_cases.Registration
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/register")
class RegisterCtrl(
    private val registration: Registration
) {

    @PostMapping
    fun register(@RequestBody req: RegisterReq): RegisterRes {
        req.validate()
        val saved = registration.register(req.toRegistered())
        return RegisterRes(saved.id)
    }

    @GetMapping("/{id}")
    fun getRegisterState(@PathVariable id: UUID): RegisterStateRes {
        val registered = registration.get(id)
        return RegisterStateRes(registered.telephone, registered.nbSmsSent)
    }

    @PostMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun validateToken(@PathVariable id: UUID, @RequestBody tokenReq: TokenValidReq) {
        registration.validateToken(id, tokenReq.code)
    }

    private fun RegisterReq.toRegistered(id: UUID = UUID.randomUUID()) = Registered(id, lastname, firstname, email, telephone)

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleENFE(e: EntityNotFoundException) = RegisterRes(error = "Une erreur est survenue")

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIAE(e: IllegalArgumentException) = RegisterRes(error = e.message)
}
