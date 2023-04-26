package org.breizhcamp.bilhed.domain.use_cases.ports

import org.breizhcamp.bilhed.domain.entities.Registered

interface SmsPort {
    fun sendRegistered(registered: Registered)
}