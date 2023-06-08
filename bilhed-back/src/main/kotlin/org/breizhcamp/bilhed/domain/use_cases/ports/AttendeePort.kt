package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.AttendeeData
import java.util.*

interface AttendeePort {

    fun saveData(id: UUID, data: AttendeeData)

    fun setPayed(ids: List<UUID>)
}