package org.breizhcamp.bilhed.config

import com.fasterxml.jackson.databind.ObjectMapper
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
    fun jackson2JsonMessageConverter(objectMapper: ObjectMapper): MessageConverter = Jackson2JsonMessageConverter(objectMapper)

    @Bean
    fun defineConnectionNameStrategy(): ConnectionNameStrategy? {
        return ConnectionNameStrategy { "bilhed-back#$instanceId:" + connectionNumber.getAndIncrement() }
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory, jackson2JsonMessageConverter: MessageConverter): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = jackson2JsonMessageConverter
        return rabbitTemplate
    }

    @Bean
    fun smsContainerFactory(rabbitConnectionFactory: ConnectionFactory): RabbitListenerContainerFactory<SimpleMessageListenerContainer> {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(rabbitConnectionFactory)
        factory.setPrefetchCount(10)
        factory.setConcurrentConsumers(1)
        return factory
    }
}