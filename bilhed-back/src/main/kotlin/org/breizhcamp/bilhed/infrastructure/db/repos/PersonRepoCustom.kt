package org.breizhcamp.bilhed.infrastructure.db.repos

import org.breizhcamp.bilhed.domain.entities.PersonFilter
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDB

interface PersonRepoCustom {

    fun filterPerson(filter: PersonFilter): List<PersonDB>

}