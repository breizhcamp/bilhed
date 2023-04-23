package org.breizhcamp.bilhed.application.rest.controllers

import org.breizhcamp.bilhed.application.rest.dto.RegisterReq
import org.breizhcamp.bilhed.application.rest.dto.RegisterRes
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/register")
class RegisterCtrl {

    @PostMapping
    fun register(@RequestBody req: RegisterReq): RegisterRes {
        try {
            req.validate()
        } catch (e: IllegalArgumentException) {
            return RegisterRes(error = e.message)
        }

        Thread.sleep(3000)
        return RegisterRes(UUID.randomUUID().toString())
    }

}