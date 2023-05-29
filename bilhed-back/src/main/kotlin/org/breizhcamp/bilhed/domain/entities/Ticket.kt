package org.breizhcamp.bilhed.domain.entities

data class Ticket(
    val payUrl: String,
    val payStatus: PayStatus,
)

enum class PayStatus {
    TO_PAY, PAYED
}
