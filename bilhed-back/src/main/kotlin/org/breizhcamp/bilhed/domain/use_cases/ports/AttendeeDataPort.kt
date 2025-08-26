package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.AttendeeData
import org.breizhcamp.bilhed.domain.entities.Person
import java.util.*

interface AttendeeDataPort {

    fun saveData(id: UUID, data: AttendeeData)

    fun getData(id: UUID): AttendeeData?

    fun listWithData(): List<Pair<Person, AttendeeData?>>

}