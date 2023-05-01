package org.breizhcamp.bilhed.sms

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BilhedSmsApplication

fun main(args: Array<String>) {
	runApplication<BilhedSmsApplication>(*args)
}
