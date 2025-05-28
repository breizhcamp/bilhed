package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.ports.ReminderPort
import org.breizhcamp.bilhed.infrastructure.mail.MailAdapter
import org.breizhcamp.bilhed.infrastructure.sms.SmsAdapter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class SendNotification(
    private val smsAdapter: SmsAdapter,
    private val reminderPort: ReminderPort,
    private val mailAdapter: MailAdapter,
) {
    @Transactional
    fun sendSms(sms: Sms, origin: ReminderOrigin) {
        reminderPort.save(Reminder(
            reminderDate = ZonedDateTime.now(),
            template = sms.template,
            method =  ReminderMethod.SMS,
            personId = sms.id,
            model = sms.model,
            origin = origin,
        ))
        smsAdapter.send(sms)
    }

    @Transactional
    fun sendEmail(mail: Mail, origin: ReminderOrigin) {
        reminderPort.save(Reminder(
            reminderDate = ZonedDateTime.now(),
            template = mail.template,
            method =  ReminderMethod.MAIL,
            personId = mail.personId,
            model = mail.model,
            origin = origin,
        ))
        mailAdapter.send(mail)
    }
}