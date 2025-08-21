package org.breizhcamp.bilhed.domain.use_cases

import org.breizhcamp.bilhed.domain.entities.*
import org.breizhcamp.bilhed.domain.use_cases.ports.NotificationPort
import org.breizhcamp.bilhed.infrastructure.mail.MailAdapter
import org.breizhcamp.bilhed.infrastructure.sms.SmsAdapter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class SendNotification(
    private val smsAdapter: SmsAdapter,
    private val notificationPort: NotificationPort,
    private val mailAdapter: MailAdapter,
) {
    @Transactional
    fun sendSms(sms: Sms, origin: NotifOrigin) {
        notificationPort.save(Notification(
            date = ZonedDateTime.now(),
            template = sms.template,
            method =  NotifMethod.SMS,
            personId = sms.id,
            model = sms.model,
            origin = origin,
        ))
        smsAdapter.send(sms)
    }

    @Transactional
    fun sendEmail(mail: Mail, origin: NotifOrigin) {
        notificationPort.save(Notification(
            date = ZonedDateTime.now(),
            template = mail.template,
            method =  NotifMethod.MAIL,
            personId = mail.personId,
            model = mail.model,
            origin = origin,
        ))
        mailAdapter.send(mail)
    }
}