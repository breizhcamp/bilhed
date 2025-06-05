package org.breizhcamp.bilhed.application.cron

import org.breizhcamp.bilhed.domain.use_cases.AttendeeSyncPayed
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class AttendeePayedCron(
    private val attendeeSyncPayed: AttendeeSyncPayed,
) {

    /** Sync paid status every one hour */
//    @Scheduled(fixedRate = 60 * 60, timeUnit = TimeUnit.SECONDS, initialDelay = 5)
    fun sync() {
        attendeeSyncPayed.sync()
    }

}