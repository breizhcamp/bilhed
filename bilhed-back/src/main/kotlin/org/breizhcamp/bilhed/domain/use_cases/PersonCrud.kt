package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.PersonFilter
import org.breizhcamp.bilhed.domain.use_cases.ports.PersonPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class PersonCrud(
    private val personPort: PersonPort
) {
    fun filter(filter: PersonFilter): List<Person> = personPort.filter(filter)

    fun get(id: UUID): Person = personPort.get(id)
}