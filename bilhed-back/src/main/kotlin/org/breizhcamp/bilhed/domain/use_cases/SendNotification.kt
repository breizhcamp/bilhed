package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.ports.ReminderPort
import org.breizhcamp.bilhed.infrastructure.db.mappers.toMQ
import org.breizhcamp.bilhed.infrastructure.mail.MailAdapter
import org.breizhcamp.bilhed.infrastructure.sms.SmsAdapter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class SendNotification(
    private val smsAdapter: SmsAdapter,
    private val reminderPort: ReminderPort,
    private val mailAdapter: MailAdapter
) {
    @Transactional
    fun sendSms(sms: Sms, origin: ReminderOrigin) {
        reminderPort.save(Reminder(
            ZonedDateTime.now(),
            sms.template,
            ReminderMethod.SMS,
            sms.id,
            sms.model,
            origin,
        ))
        smsAdapter.send(sms.toMQ())
    }

    @Transactional
    fun sendEmail(mail: Mail, origin: ReminderOrigin) {
        mail.personIds.forEach { personId ->
            reminderPort.save(Reminder(
                ZonedDateTime.now(),
                mail.template,
                ReminderMethod.MAIL,
                personId,
                mail.model,
                origin,
            ))
        }
        mailAdapter.send(mail.toMQ())
    }
}