package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.Attendee
import org.breizhcamp.bilhed.domain.entities.AttendeeFilter
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.springframework.stereotype.Service

@Service
class AttendeeList(
    private val attendeePort: AttendeePort,
) {

    fun filter(filter: AttendeeFilter): List<Attendee> = attendeePort.filter(filter)

}