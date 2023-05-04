package org.breizhcamp.bilhed.mail

import org.breizhcamp.bilhed.mail.config.BilhedMailConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(BilhedMailConfig::class)
class BilhedMailApplication

fun main(args: Array<String>) {
	runApplication<BilhedMailApplication>(*args)
}
