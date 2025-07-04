package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.application.dto.admin.UpdateContactReq
import org.breizhcamp.bilhed.domain.entities.Person
import java.util.*

interface PersonPort {

    fun get(id: UUID): Person

    fun updateContact(id: UUID, updateContactReq: UpdateContactReq)

}