package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.Group
import org.breizhcamp.bilhed.infrastructure.db.model.GroupDB

fun Group.toDB() = GroupDB(
    id = this.id,
    referentId = this.referentId,
    groupPayment = this.groupPayment,
    drawOrder = this.drawOrder,
)

fun GroupDB.toGroup() = Group(
    id = this.id,
    referentId = this.referentId,
    groupPayment = this.groupPayment,
    drawOrder = this.drawOrder,
)