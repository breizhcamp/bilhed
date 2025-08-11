package org.breizhcamp.bilhed.infrastructure.db.mappers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.core.type.TypeReference
import org.breizhcamp.bilhed.domain.entities.Notification
import org.breizhcamp.bilhed.domain.entities.NotifMethod
import org.breizhcamp.bilhed.domain.entities.NotifOrigin
import org.breizhcamp.bilhed.infrastructure.db.model.NotificationDB
import org.breizhcamp.bilhed.infrastructure.db.model.NotifDBMethod
import org.breizhcamp.bilhed.infrastructure.db.model.NotifDBOrigin

val mapper = jacksonObjectMapper()
val typeRef = object : TypeReference<Map<String, String>>() {}

fun Notification.toDB() = NotificationDB(
    id = this.id,
    reminderDate = this.reminderDate,
    template = this.template,
    method = this.method.toDB(),
    personId = this.personId,
    model = mapper.writeValueAsString(this.model),
    origin = this.origin.toDB()
)

fun NotificationDB.toNotification() = Notification(
    id = this.id,
    reminderDate = this.reminderDate,
    template = this.template,
    method = this.method.toNotifMethod(),
    personId = this.personId,
    model = mapper.readValue(this.model, typeRef),
    origin = this.origin.toNotifOrigin()
)

fun NotifDBOrigin.toNotifOrigin() = NotifOrigin.valueOf(this.name)

fun NotifOrigin.toDB() = NotifDBOrigin.valueOf(this.name)

fun NotifDBMethod.toNotifMethod() = NotifMethod.valueOf(this.name)

fun NotifMethod.toDB() = NotifDBMethod.valueOf(this.name)