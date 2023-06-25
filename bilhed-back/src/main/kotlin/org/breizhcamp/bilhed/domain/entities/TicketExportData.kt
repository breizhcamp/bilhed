package org.breizhcamp.bilhed.domain.entities

import java.util.UUID

data class TicketExportData(
    val id: UUID?,
    val ticketType: String,
    val barcode: String,
    val lastname: String,
    val firstname: String,
    val email: String,
    val company: String?,
    val noGoodies: String?,
    val tShirtSize: String?,
    val tShirtFitting: String?,
)