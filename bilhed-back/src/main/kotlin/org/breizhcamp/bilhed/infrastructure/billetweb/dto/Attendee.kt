package org.breizhcamp.bilhed.infrastructure.billetweb.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * A BilletWeb attendee
 */
data class Attendee(
        val id: String,
        val firstname: String,
        val name: String,
        val email: String,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("last_update")
        val lastUpdate: LocalDateTime,

        @JsonProperty("product_management")
        val productManagement: String,
        val ticket: String,
        @JsonProperty("order_id")
        val orderId: String,
        @JsonProperty("order_ext_id")
        val orderExtId: String,
        @JsonProperty("order_firstname")
        val orderFirstname: String,
        @JsonProperty("order_name")
        val orderName: String,
        @JsonProperty("order_email")
        val orderEmail: String,
        @JsonProperty("order_management")
        val orderManagement: String,
)