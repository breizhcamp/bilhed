package org.breizhcamp.bilhed.infrastructure.billetweb.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateReq(
    val data: List<CreateCmd>,
) {
    constructor(cmd: CreateCmd) : this(listOf(cmd))
}

data class CreateCmd(
    val name: String,
    val firstname: String,
    val email: String,

    @JsonProperty("payment_type")
    val paymentType: String,

    val products: List<CreateProduct>,
)

data class CreateProduct(
    val ticket: String,

    val name: String,
    val firstname: String,
    val email: String,
)