package org.breizhcamp.bilhed.mail.application.amqp

import mu.KotlinLogging
import org.breizhcamp.bilhed.mail.application.amqp.dto.MailAddressDTO
import org.breizhcamp.bilhed.mail.application.amqp.dto.MailFromBack
import org.breizhcamp.bilhed.mail.config.BilhedMailConfig
import org.breizhcamp.bilhed.mail.domain.entities.Mail
import org.breizhcamp.bilhed.mail.domain.entities.MailAddress
import org.breizhcamp.bilhed.mail.domain.use_cases.MailSending
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class MailReceiver(
    private val mailSending: MailSending,
    private val mailConfig: BilhedMailConfig,
) {

    @RabbitListener(queues = ["mail-send-queue"], containerFactory = "mailContainerFactory")
    fun receive(mail: MailFromBack) {
        logger.info { "Received mail from backend to [${mail.to}] with template [${mail.template}]" }
        mailSending.send(mail.toMail(mailConfig.from))
    }

}

private fun MailFromBack.toMail(from: MailAddressDTO) = Mail(
    from = from.toMailAddress(),
    to = to.map { it.toMailAddress() },
    template = template,
    model = model,
)

private fun MailAddressDTO.toMailAddress() = MailAddress(email, name)
