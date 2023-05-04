package org.breizhcamp.bilhed.mail.infrastructure.mail

import jakarta.mail.internet.InternetAddress
import org.breizhcamp.bilhed.mail.domain.entities.Mail
import org.breizhcamp.bilhed.mail.domain.entities.MailAddress
import org.breizhcamp.bilhed.mail.domain.use_cases.port.EmailSenderPort
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import java.util.*

@Service
class EmailSender(
    private val templateEngine: SpringTemplateEngine,
    private val mailSender: JavaMailSender,
): EmailSenderPort {

    override fun send(mail: Mail) {
        val ctx = Context(Locale.FRANCE, mail.model)
        val subject = templateEngine.process("emails/${mail.template}/email_subject.txt", ctx)
        val bodyText = templateEngine.process("emails/${mail.template}/email.txt", ctx)
        val bodyHtml = templateEngine.process("emails/${mail.template}/email.html", ctx)

        val mimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")
        helper.setFrom(mail.from.toInternetAddress())
        mail.to.map { it.toInternetAddress() }.forEach { helper.addTo(it) }
        mail.cc.map { it.toInternetAddress() }.forEach { helper.addCc(it) }
        mail.bcc.map { it.toInternetAddress() }.forEach { helper.addBcc(it) }

        helper.setSubject(subject)
        helper.setText(bodyText, bodyHtml)

        mailSender.send(mimeMessage)
    }

}

private fun MailAddress.toInternetAddress() = InternetAddress(email, name)
