package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.springframework.stereotype.Service

@Service
class ConfigTemplate (
    val config: BilhedBackConfig
) {
    fun getEmailTemplates(): Map<String, Boolean> {
        return config.templates.mail
    }

    fun getSMSTemplates(): Map<String, Boolean> {
        return config.templates.sms
    }
}