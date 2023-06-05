package org.breizhcamp.bilhed.application.rest

import org.breizhcamp.bilhed.application.dto.AttendeeInfo
import org.breizhcamp.bilhed.domain.use_cases.AttendeeGetInfo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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

}