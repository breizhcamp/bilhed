package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.Person
import org.breizhcamp.bilhed.domain.entities.PersonStatus
import org.breizhcamp.bilhed.infrastructure.db.model.GroupDB
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDB
import org.breizhcamp.bilhed.infrastructure.db.model.PersonDBStatus

fun Person.toDB(group: GroupDB) = PersonDB(
    id = this.id,
    status = this.status.toDB(),
    lastname = this.lastname,
    firstname = this.firstname,
    telephone = this.telephone,
    email = this.email,
    pass = this.pass,
    payed = this.payed,
    group = group
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
    groupId = this.group.id
)

fun PersonStatus.toDB() = PersonDBStatus.valueOf(this.name)

fun PersonDBStatus.toStatus() = PersonStatus.valueOf(this.name)