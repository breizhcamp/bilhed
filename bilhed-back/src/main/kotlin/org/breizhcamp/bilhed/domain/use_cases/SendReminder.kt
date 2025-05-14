package org.breizhcamp.bilhed.domain.use_cases

import mu.KotlinLogging
import org.breizhcamp.bilhed.domain.entities.AttendeeFilter
import org.breizhcamp.bilhed.domain.entities.ReminderOrigin
import org.breizhcamp.bilhed.domain.use_cases.ports.AttendeePort
import org.breizhcamp.bilhed.domain.use_cases.ports.ConfigPort
import org.breizhcamp.bilhed.domain.use_cases.ports.ParticipantPort
import org.breizhcamp.bilhed.domain.use_cases.ports.RegisteredPort
import org.breizhcamp.bilhed.domain.use_cases.ports.ReminderConfigPort
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class SendReminder (
    private val registeredPort: RegisteredPort,
    private val reminderConfigPort: ReminderConfigPort,
    private val participantPort: ParticipantPort,
    private val registeredReminder: RegisteredReminder,
    private val participantNotif: ParticipantNotif,
    private val attendeeNotify: AttendeeNotify,
    private val attendeePort: AttendeePort,
    private val configPort: ConfigPort
) {

    fun sendRegisteredReminder() {
        /**
         * Send reminder if registrationDate + (max time - config time to confirm registration) == now
         */
//        logger.info { "Checking for registration reminders" }

        registeredPort.list().forEach { registered ->
            reminderConfigPort.listByType("REGISTERED").forEach { reminderConfig ->
                val registrationDate = registered.registrationDate.truncatedTo(ChronoUnit.MINUTES)
                val now = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                val maxTime = configPort.get("reminderTimeReg").value.toLong()

                if (registrationDate.plusHours((maxTime - reminderConfig.hours.toLong())).isEqual(now)) {
                    registeredReminder.send(
                        registered.id,
                        reminderConfig.templateSms,
                        reminderConfig.templateMail,
                        ReminderOrigin.AUTOMATIC
                    )
                }
            }
        }
    }

    fun sendParticipantReminder() {
        /**
         * Send reminder if confirmationSentDate + (max time - config time to confirm participation) == now
         */
//        logger.info { "Checking for participant draw success reminders" }

        val idsToRemind = mutableListOf<UUID>()

        participantPort.list().forEach { participant ->
            if (participant.notificationConfirmDate != null) { // draw done?
                val mailConfirmDate = participant.notificationConfirmDate.truncatedTo(ChronoUnit.MINUTES)
                val now = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                val maxTime = configPort.get("reminderTimePar").value.toLong()
                reminderConfigPort.listByType("PARTICIPANT").forEach { reminderConfig ->
                    if (mailConfirmDate.plusHours((maxTime - reminderConfig.hours.toLong())).isEqual(now)) {
                        idsToRemind.add(participant.id)
                    }
                }
            }
        }
        participantNotif.remindSuccess(idsToRemind, ReminderOrigin.AUTOMATIC)
    }

    fun sendAttendeeReminder() {
        logger.info { "Checking for attendee payed reminders" }
        val idsToRemindSms = mutableListOf<UUID>()
        val idsToRemindMail = mutableListOf<UUID>()

        attendeePort.filter(AttendeeFilter(null, null, null, null, false)).forEach { attendee ->
            val participantConfirmDate = attendee.participantConfirmationDate.truncatedTo(ChronoUnit.MINUTES)
            val now = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES)
            val maxTime = configPort.get("reminderTimeAtt").value.toLong()

            reminderConfigPort.listByType("ATTENDEE").forEach { reminderConfig ->
                if (participantConfirmDate.plusHours((maxTime - reminderConfig.hours.toLong())).isEqual(now)) {
                    if (!reminderConfig.templateMail.isBlank()) {
                        idsToRemindMail.add(attendee.id)
                    }
                    if (!reminderConfig.templateSms.isBlank()) {
                        idsToRemindSms.add(attendee.id)
                    }
                }
            }
        }
        attendeeNotify.remindPayedMail(idsToRemindMail, ReminderOrigin.AUTOMATIC)
        attendeeNotify.remindPayedSms(idsToRemindSms, ReminderOrigin.AUTOMATIC)
    }
}