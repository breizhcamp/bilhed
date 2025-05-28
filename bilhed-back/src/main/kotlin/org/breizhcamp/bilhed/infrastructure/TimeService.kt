package org.breizhcamp.bilhed.infrastructure

import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class TimeService {

    fun now(): ZonedDateTime = ZonedDateTime.now()
}