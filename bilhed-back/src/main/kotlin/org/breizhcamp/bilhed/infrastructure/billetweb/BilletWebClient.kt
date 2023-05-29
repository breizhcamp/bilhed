package org.breizhcamp.bilhed.infrastructure.billetweb

import org.breizhcamp.bilhed.infrastructure.billetweb.dto.Attendee
import org.breizhcamp.bilhed.infrastructure.billetweb.dto.CreateReq
import org.breizhcamp.bilhed.infrastructure.billetweb.dto.CreateRes
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange

interface BilletWebClient {

    @PostExchange("/api/event/{eventId}/add_order")
    fun create(@PathVariable eventId: String, @RequestBody req: CreateReq): List<CreateRes>

    @GetExchange("/api/event/{eventId}/attendees")
    fun listAttendees(@PathVariable eventId: String, @RequestParam("last_update") lastUpdate: Long): List<Attendee>

}