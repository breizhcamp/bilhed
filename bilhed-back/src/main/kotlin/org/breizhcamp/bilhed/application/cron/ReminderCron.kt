package org.breizhcamp.bilhed.application.cron

import org.breizhcamp.bilhed.domain.use_cases.PersonRelease
import org.breizhcamp.bilhed.domain.use_cases.SendReminder
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ReminderCron (
    private val sendReminder: SendReminder,
    private val personRelease: PersonRelease
) {

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES, initialDelay = 1)
    fun checkReminders() {
        personRelease.participantReleaseAuto()
        personRelease.attendeeReleaseAuto()

        sendReminder.sendRegisteredReminder()
        sendReminder.sendParticipantReminder()
        sendReminder.sendAttendeeReminder()
    }
}