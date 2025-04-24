package org.breizhcamp.bilhed.infrastructure.db.mappers

import org.breizhcamp.bilhed.domain.entities.ReminderConfig
import org.breizhcamp.bilhed.infrastructure.db.model.ReminderConfigDB

fun ReminderConfigDB.toReminderConfig() = ReminderConfig(
    id = this.id,
    type = this.type,
    hours = this.hours,
    templateMail = this.templateMail,
    templateSms = this.templateSms,
)

fun ReminderConfig.toDB() = ReminderConfigDB(
    id = this.id,
    type = this.type,
    hours = this.hours,
    templateMail = this.templateMail,
    templateSms = this.templateSms,
)
