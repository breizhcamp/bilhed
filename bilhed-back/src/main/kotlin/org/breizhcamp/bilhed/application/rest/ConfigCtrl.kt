package org.breizhcamp.bilhed.application.rest

import org.breizhcamp.bilhed.application.dto.ConfigRes
import org.breizhcamp.bilhed.domain.use_cases.RegistrationDateOpen
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ConfigCtrl(
    private val registrationDateOpen: RegistrationDateOpen,
) {

    @GetMapping("/config")
    fun config(): ConfigRes = ConfigRes(
        closeDate = registrationDateOpen.getCloseDate()
    )

}