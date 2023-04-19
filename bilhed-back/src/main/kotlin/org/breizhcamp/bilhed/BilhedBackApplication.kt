package org.breizhcamp.bilhed

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BilhedBackApplication

fun main(args: Array<String>) {
	runApplication<BilhedBackApplication>(*args)
}
