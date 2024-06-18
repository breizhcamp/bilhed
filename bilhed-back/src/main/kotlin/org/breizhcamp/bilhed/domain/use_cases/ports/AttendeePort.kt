package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Attendee
import org.breizhcamp.bilhed.domain.entities.AttendeeData
import org.breizhcamp.bilhed.domain.entities.AttendeeFilter
import java.util.*

interface AttendeePort {

    fun filter(filter: AttendeeFilter): List<Attendee>

    fun get(id: UUID): Attendee

    fun get(ids: List<UUID>): List<Attendee>

    fun saveData(id: UUID, data: AttendeeData)

    fun getData(id: UUID): AttendeeData?

    fun setPayed(ids: List<UUID>)

    fun listWithData(): List<Pair<Attendee, AttendeeData?>>

    fun levelUpToReleased(id: UUID)
}