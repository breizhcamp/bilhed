package org.breizhcamp.bilhed.sms.config

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

    @Bean
    fun smsQueue(): Queue = QueueBuilder.durable("sms-send-queue").build()

    @Bean
    fun smsEx(): DirectExchange = ExchangeBuilder
        .directExchange("sms-send")
        .durable(true)
        .build()

    @Bean
    fun smsBinding(): Binding = BindingBuilder
        .bind(smsQueue())
        .to(smsEx())
        .with("")


    @Bean
    fun jackson2JsonMessageConverter(objectMapper: ObjectMapper): MessageConverter = Jackson2JsonMessageConverter(objectMapper)

    @Bean
    fun defineConnectionNameStrategy(): ConnectionNameStrategy? {
        return ConnectionNameStrategy { "bilhed-sms#$instanceId:" + connectionNumber.getAndIncrement() }
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory, jackson2JsonMessageConverter: MessageConverter): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = jackson2JsonMessageConverter
        return rabbitTemplate
    }

    @Bean
    fun smsContainerFactory(rabbitConnectionFactory: ConnectionFactory, jackson2JsonMessageConverter: MessageConverter): RabbitListenerContainerFactory<SimpleMessageListenerContainer> {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(rabbitConnectionFactory)
        factory.setPrefetchCount(10)
        factory.setConcurrentConsumers(1)
        factory.setMessageConverter(jackson2JsonMessageConverter)
        return factory
    }
}