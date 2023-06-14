package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class RegistrationDateOpen(
    val config: BilhedBackConfig
) {

    fun getCloseDate(): ZonedDateTime {
        Thread.sleep(1000)
        return config.registerCloseDate
    }

}