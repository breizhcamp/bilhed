package org.breizhcamp.bilhed.application.rest.admin

import org.breizhcamp.bilhed.application.dto.admin.RegisteredDTO
import org.breizhcamp.bilhed.domain.entities.Registered
import org.breizhcamp.bilhed.domain.use_cases.RegisteredImport
import org.breizhcamp.bilhed.domain.use_cases.RegisteredList
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/admin/registered")
class RegisteredCtrl(
    private val registeredList: RegisteredList,
    private val registeredImport: RegisteredImport,
) {

    @GetMapping
    fun listRegistered(): List<RegisteredDTO> = registeredList.list().map { it.toDTO() }

    @PostMapping("/import")
    fun importCsv(file: MultipartFile) {
        registeredImport.importCsv(file.inputStream)
    }

    @PostMapping("/level-up")
    fun levelUp(@RequestBody ids: List<UUID>) {
        registeredImport.levelUp(ids)
    }
}

fun Registered.toDTO() = RegisteredDTO(
    id = id,

    lastname = lastname,
    firstname = firstname,
    email = email,
    telephone = telephone,
    pass = pass,
    kids = kids,

    registrationDate = registrationDate,

    smsStatus = smsStatus,
    nbSmsSent = nbSmsSent,
    lastSmsSentDate = lastSmsSentDate,
    smsError = smsError,
    token = token,
    nbTokenTries = nbTokenTries,
)