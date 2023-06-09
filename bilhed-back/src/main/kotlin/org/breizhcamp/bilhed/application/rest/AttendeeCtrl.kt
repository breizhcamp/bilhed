package org.breizhcamp.bilhed.application.rest

import jakarta.persistence.EntityNotFoundException
import org.breizhcamp.bilhed.application.dto.AttendeeInfo
import org.breizhcamp.bilhed.application.dto.ErrorRes
import org.breizhcamp.bilhed.domain.use_cases.AttendeeGetInfo
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/attendees")
class AttendeeCtrl(
    private val attendeeGetInfo: AttendeeGetInfo,
) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): AttendeeInfo {
        return AttendeeInfo(attendeeGetInfo.getPaymentUrl(id))
    }

    @ExceptionHandler(EntityNotFoundException::class) @ResponseStatus(HttpStatus.NO_CONTENT)
    fun handleEntityNotFoundException(e: EntityNotFoundException) {}

}