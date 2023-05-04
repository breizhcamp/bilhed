package org.breizhcamp.bilhed.mail.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.*
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


@Configuration
class AmqpConfig {
    private val instanceId = UUID.randomUUID().toString().substring(0, 8)
    private val connectionNumber = AtomicInteger(0)

    /* *****   MAIL RECEIVED FROM BACKEND   ***** */
    @Bean
    fun mailQueue(): Queue = QueueBuilder.durable("mail-send-queue")
        .withArgument("x-dead-letter-exchange", "mail-send-error")
        .withArgument("x-dead-letter-routing-key", "")
        .build()

    @Bean
    fun mailEx(): DirectExchange = ExchangeBuilder.directExchange("mail-send").durable(true).build()

    @Bean
    fun mailBinding(): Binding = BindingBuilder.bind(mailQueue()).to(mailEx()).with("")


    /* *****   MAIL RECEIVED FROM BACKEND / DEAD LETTER EXCHANGE   ***** */
    @Bean
    fun mailErrorQueue(): Queue = QueueBuilder.durable("mail-send-error-queue").build()

    @Bean
    fun mailErrorEx(): DirectExchange = ExchangeBuilder.directExchange("mail-send-error").durable(true).build()

    @Bean
    fun mailErrorBinding(): Binding = BindingBuilder.bind(mailErrorQueue()).to(mailErrorEx()).with("")


    /* *****   RABBITMQ CONFIG   ***** */
    @Bean
    fun jackson2JsonMessageConverter(objectMapper: ObjectMapper): MessageConverter = Jackson2JsonMessageConverter(objectMapper)

    @Bean
    fun defineConnectionNameStrategy(): ConnectionNameStrategy? {
        return ConnectionNameStrategy { "bilhed-mail#$instanceId:" + connectionNumber.getAndIncrement() }
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory, jackson2JsonMessageConverter: MessageConverter): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = jackson2JsonMessageConverter
        return rabbitTemplate
    }

    @Bean
    fun mailContainerFactory(rabbitConnectionFactory: ConnectionFactory, jackson2JsonMessageConverter: MessageConverter): RabbitListenerContainerFactory<SimpleMessageListenerContainer> {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(rabbitConnectionFactory)
        factory.setPrefetchCount(10)
        factory.setConcurrentConsumers(1)
        factory.setMessageConverter(jackson2JsonMessageConverter)
        factory.setDefaultRequeueRejected(false)
        return factory
    }
}