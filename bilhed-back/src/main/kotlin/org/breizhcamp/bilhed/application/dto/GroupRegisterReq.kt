package org.breizhcamp.bilhed.application.dto

data class GroupRegisterReq(
    val referent: ReferentRegisterReq,
    val groupPayment: Boolean,
    val companions: List<CompanionRegisterReq>
) {
    fun validate(groupPayment: Boolean) {
        referent.validate(groupPayment, true)
        companions.forEach { it.validate(groupPayment) }
    }
}
