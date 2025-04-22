package org.breizhcamp.bilhed.infrastructure.db.mappers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.core.type.TypeReference
import org.breizhcamp.bilhed.domain.entities.Reminder
import org.breizhcamp.bilhed.domain.entities.ReminderMethod
import org.breizhcamp.bilhed.domain.entities.ReminderOrigin
import org.breizhcamp.bilhed.infrastructure.db.model.ReminderDB
import org.breizhcamp.bilhed.infrastructure.db.model.ReminderDBMethod
import org.breizhcamp.bilhed.infrastructure.db.model.ReminderDBOrigin

val mapper = jacksonObjectMapper()
val typeRef = object : TypeReference<Map<String, String>>() {}

fun Reminder.toDB() = ReminderDB(
    id = this.id,
    reminderDate = this.reminderDate,
    template = this.template,
    method = this.method.toDB(),
    personId = this.personId,
    model = mapper.writeValueAsString(this.model),
    origin = this.origin.toDB()
)

fun ReminderDB.toReminder() = Reminder(
    id = this.id,
    reminderDate = this.reminderDate,
    template = this.template,
    method = this.method.toReminderMethod(),
    personId = this.personId,
    model = mapper.readValue(this.model, typeRef),
    origin = this.origin.toReminderOrigin()
)

fun ReminderDBOrigin.toReminderOrigin() = ReminderOrigin.valueOf(this.name)

fun ReminderOrigin.toDB() = ReminderDBOrigin.valueOf(this.name)

fun ReminderDBMethod.toReminderMethod() = ReminderMethod.valueOf(this.name)

fun ReminderMethod.toDB() = ReminderDBMethod.valueOf(this.name)