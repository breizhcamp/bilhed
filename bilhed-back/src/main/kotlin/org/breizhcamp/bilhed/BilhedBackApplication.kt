package org.breizhcamp.bilhed

import org.breizhcamp.bilhed.config.BilhedBackConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(BilhedBackConfig::class)
class BilhedBackApplication

fun main(args: Array<String>) {
	runApplication<BilhedBackApplication>(*args)
}
