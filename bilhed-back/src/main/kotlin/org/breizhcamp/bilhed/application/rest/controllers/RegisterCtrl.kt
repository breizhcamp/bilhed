package org.breizhcamp.bilhed.application.rest.controllers

import org.breizhcamp.bilhed.application.rest.dto.RegisterReq
import org.breizhcamp.bilhed.application.rest.dto.RegisterRes
import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.use_cases.Registration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/register")
class RegisterCtrl(
    private val registration: Registration
) {

    @PostMapping
    fun register(@RequestBody req: RegisterReq): RegisterRes {
        return try {
            req.validate()
            val id = UUID.randomUUID().toString()
            registration.register(req.toRegistered(id))
            RegisterRes(id)
        } catch (e: IllegalArgumentException) {
            RegisterRes(error = e.message)
        }
    }

    private fun RegisterReq.toRegistered(id: String) = Registered(id, lastname, firstname, email, telephone)
}
