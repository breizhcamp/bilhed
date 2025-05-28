package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.ports.*
import org.breizhcamp.bilhed.infrastructure.TimeService
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.*


@Service
class SendReminder (
    private val registeredPort: RegisteredPort,
    private val reminderPort: ReminderPort,
    private val reminderConfigPort: ReminderConfigPort,
    private val participantPort: ParticipantPort,
    private val registeredReminder: RegisteredReminder,
    private val participantNotif: ParticipantNotif,
    private val attendeeNotify: AttendeeNotify,
    private val attendeePort: AttendeePort,
    private val configPort: ConfigPort,
    private val timeService: TimeService,
) {

    private fun areConditionsMet(deadline: ZonedDateTime, reminderConfigs: List<ReminderConfig>, now: ZonedDateTime, notif: Reminder?): ReminderConfig? {
        val (prevRemConfig, nextRemDate) = reminderConfigs
            .map { it to deadline.minusHours(it.hours.toLong()) } // map en config -> date
            .partition { (_, configDate) -> configDate.isBefore(now) } // separe en deux listes : passées et futures
            .let { (past, future) ->
                val prev = past.maxByOrNull { it.second } // prev plus proche de now
                val next = future.minByOrNull { it.second } // next plus proche de now
                prev?.first to next?.second
            }

        // si prochain rappel && heure prochain rappel < now + 55min
        if (nextRemDate?.isBefore(now.plusMinutes(55)) != false) return null

        // si précédente notif envoyée == null (n'arrive pas, il aura normalement toujours une notification dans son historique).
        val prevNotif = notif ?: return null

        if (prevRemConfig == null) return null
        // si date précédente config <= date précédente notif envoyé < now
        val prevRemConfigDate = deadline.minusHours(prevRemConfig.hours.toLong())
        if (prevNotif.reminderDate.isBefore(now) && (
                prevRemConfigDate.isBefore(prevNotif.reminderDate) || prevRemConfigDate.isEqual(prevNotif.reminderDate)
            )) return null

        return prevRemConfig
    }

    private fun getNotifications(persons: List<Person>):  Map<UUID, Reminder> {
        val notificationsList = reminderPort.findLatestReminderPerPerson(persons.map { it.id })
        return notificationsList.associateBy { it.personId }
    }

    fun sendRegisteredReminder() {
        val now = timeService.now()
        val maxTime = configPort.get("reminderTimeReg").value.toLong()

        val reminderConfigs = reminderConfigPort.listByType("REGISTERED")
        var registers = registeredPort.list()
        // On filtre ceux dont la deadline est dépassée (car ils ne sont pas RELEASED).
        registers = registers.filter { it.registrationDate.plusHours(maxTime).isAfter(now) }

        val notifications = getNotifications(registers)

        for (reg in registers) {
            val deadline = reg.registrationDate.plusHours(maxTime)
            val prevRemConfig = areConditionsMet(deadline, reminderConfigs, now, notifications[reg.id]) ?: break

            registeredReminder.send(
                reg.id,
                prevRemConfig.templateSms,
                prevRemConfig.templateMail,
                ReminderOrigin.AUTOMATIC
            )
        }
    }

    fun sendParticipantReminder() {
        val now = timeService.now()
        val maxTime = configPort.get("reminderTimePar").value.toLong()

        val reminderConfigs = reminderConfigPort.listByType("PARTICIPANT")
        val participants = participantPort.list()
        val notifications = getNotifications(participants)

        for (par in participants) {
            val deadline = par.notificationConfirmDate?.plusHours(maxTime)
            if (deadline == null) break // tirage au sort pas encore effectué
            val prevRemConfig = areConditionsMet(deadline, reminderConfigs, now, notifications[par.id]) ?: break

            participantNotif.remindSuccess(listOf(par.id), ReminderOrigin.AUTOMATIC, prevRemConfig.templateMail)
        }
    }

    fun sendAttendeeReminder() {
        val now = timeService.now()
        val maxTime = configPort.get("reminderTimeAtt").value.toLong()

        val reminderConfigs = reminderConfigPort.listByType("ATTENDEE")
        val attendees = attendeePort.filter(AttendeeFilter(null, null, null, null, false))
        val notifications = getNotifications(attendees)

        for (att in attendees) {
            val deadline = att.participantConfirmationDate.plusHours(maxTime)
            val prevRemConfig = areConditionsMet(deadline, reminderConfigs, now, notifications[att.id]) ?: break

            if (prevRemConfig.templateMail.isNotBlank())
                attendeeNotify.remindPayedMail(listOf(att.id), ReminderOrigin.AUTOMATIC, prevRemConfig.templateMail)

            if (prevRemConfig.templateSms.isNotBlank())
                attendeeNotify.remindPayedSms(listOf(att.id), ReminderOrigin.AUTOMATIC, prevRemConfig.templateSms)
        }
    }
}