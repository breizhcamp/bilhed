package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class ConfigDate (
    val config: BilhedBackConfig
) {

    fun getRegistrationCloseDate(): ZonedDateTime {
        return config.registerCloseDate
    }


    fun getBreizhCampCloseDate(): ZonedDateTime {
        return config.breizhCampCloseDate
    }

}