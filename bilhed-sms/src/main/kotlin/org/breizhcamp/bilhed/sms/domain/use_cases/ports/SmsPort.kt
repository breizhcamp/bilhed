package org.breizhcamp.bilhed.sms.domain.use_cases.ports

interface SmsPort {

    fun send(phone: String, message: String, tag: String)

}