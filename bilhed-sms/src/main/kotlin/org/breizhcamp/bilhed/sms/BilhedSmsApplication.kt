package org.breizhcamp.bilhed.sms

import org.breizhcamp.bilhed.sms.config.BilhedSmsConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(BilhedSmsConfig::class)
class BilhedSmsApplication

fun main(args: Array<String>) {
	runApplication<BilhedSmsApplication>(*args)
}
