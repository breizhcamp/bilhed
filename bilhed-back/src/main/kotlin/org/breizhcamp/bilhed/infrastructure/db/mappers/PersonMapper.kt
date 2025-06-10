package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDB
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDBStatus

fun Person.toDB() = PersonDB(
    id = this.id,
    status = this.status.toDB(),
    lastname = this.lastname,
    firstname = this.firstname,
    telephone = this.telephone,
    email = this.email,
    pass = this.pass,
    payed = this.payed,
    groupId = this.groupId
)

fun PersonDB.toPerson() = Person(
    id = this.id,
    status = this.status.toStatus(),
    lastname = this.lastname,
    firstname = this.firstname,
    telephone = this.telephone,
    email = this.email,
    pass = this.pass,
    payed = this.payed,
    groupId = this.groupId
)

fun PersonStatus.toDB() = PersonDBStatus.valueOf(this.name)

fun PersonDBStatus.toStatus() = PersonStatus.valueOf(this.name)