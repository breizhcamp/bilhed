package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.ports.*
import org.breizhcamp.bilhed.infrastructure.TimeService
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.*


@Service
class SendReminder (
    private val notificationPort: NotificationPort,
    private val reminderConfigPort: ReminderConfigPort,
    private val registeredReminder: RegisteredReminder,
    private val participantNotify: ParticipantNotify,
    private val attendeeNotify: AttendeeNotify,
    private val configPort: ConfigPort,
    private val timeService: TimeService,
    private val registrationInfoPort: RegistrationInfoPort,
    private val participationInfoPort: ParticipationInfoPort,
    private val personPort: PersonPort,
) {

    private fun areConditionsMet(deadline: ZonedDateTime, reminderConfigs: List<ReminderConfig>, now: ZonedDateTime, notif: Notification?): ReminderConfig? {
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
        if (prevNotif.date.isBefore(now) && (
                prevRemConfigDate.isBefore(prevNotif.date) || prevRemConfigDate.isEqual(prevNotif.date)
            )) return null

        return prevRemConfig
    }

    private fun getNotifications(ids: List<UUID>):  Map<UUID, Notification> {
        val notificationsList = notificationPort.findLatestReminderPerPerson(ids)
        return notificationsList.associateBy { it.personId }
    }

    fun sendRegisteredReminder() {
        val now = timeService.now()
        val maxTime = configPort.get("reminderTimeReg").value.toLong()

        val reminderConfigs = reminderConfigPort.listBy("REGISTERED")
        var registers = registrationInfoPort.list(PersonStatus.REGISTERED)
        // On filtre ceux dont la deadline est dépassée (car ils ne sont pas RELEASED).
        registers = registers.filter { it.registrationDate.plusHours(maxTime).isAfter(now) }

        val notifications = getNotifications(registers.map { it.personId })

        for (reg in registers) {
            val deadline = reg.registrationDate.plusHours(maxTime)
            val prevRemConfig = areConditionsMet(deadline, reminderConfigs, now, notifications[reg.personId]) ?: continue

            registeredReminder.send(
                reg.personId,
                prevRemConfig.templateSms,
                prevRemConfig.templateMail,
                NotifOrigin.AUTOMATIC
            )
        }
    }

    fun sendParticipantReminder() {
        val now = timeService.now()
        val maxTime = configPort.get("reminderTimePar").value.toLong()

        val reminderConfigs = reminderConfigPort.listBy("PARTICIPANT")
        val participants = participationInfoPort.list(PersonStatus.PARTICIPANT)
        val notifications = getNotifications(participants.map { it.personId })

        for (par in participants) {
            val deadline = par.notificationConfirmSentDate?.plusHours(maxTime)
            if (deadline == null) continue // tirage au sort pas encore effectué
            val prevRemConfig = areConditionsMet(deadline, reminderConfigs, now, notifications[par.personId]) ?: break

            participantNotify.remindSuccess(listOf(par.personId), NotifOrigin.AUTOMATIC, prevRemConfig.templateMail)
        }
    }

    fun sendAttendeeReminder() {
        val now = timeService.now()
        val maxTime = configPort.get("reminderTimeAtt").value.toLong()

        val reminderConfigs = reminderConfigPort.listBy("ATTENDEE")
        val attendees = personPort.filter(PersonFilter(status = PersonStatus.ATTENDEE))
        val partInfo = participationInfoPort.get(attendees.map { it.id })

        val notPayed = partInfo
            .filter { !it.payed }
            .associateBy { it.personId }

        val attendeesNotPayed = attendees.filter { it.id in notPayed.keys }

        val notifications = getNotifications(attendeesNotPayed.map { it.id })

        for (att in attendeesNotPayed) {
            val infos = notPayed[att.id] ?: continue
            if (infos.confirmationDate == null) continue

            val deadline = infos.confirmationDate.plusHours(maxTime)
            val prevRemConfig = areConditionsMet(deadline, reminderConfigs, now, notifications[att.id]) ?: continue

            if (prevRemConfig.templateMail.isNotBlank())
                attendeeNotify.remindPayedMail(listOf(att.id), NotifOrigin.AUTOMATIC, prevRemConfig.templateMail)

            if (prevRemConfig.templateSms.isNotBlank())
                attendeeNotify.remindPayedSms(listOf(att.id), NotifOrigin.AUTOMATIC, prevRemConfig.templateSms)
        }
    }
}