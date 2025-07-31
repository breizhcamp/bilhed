package org.breizhcamp.bilhed.application.dto

data class GroupRegisterReq(
    val referent: ReferentRegisterReq,
    val groupPayment: Boolean,
    val companions: List<CompanionRegisterReq>
) {
    fun validate(groupPayment: Boolean): Boolean {
        // GroupPayment is TRUE for a single Person
        val gPayment = if (companions.isEmpty()) true else groupPayment
        referent.validate(gPayment, true)
        companions.forEach { it.validate(gPayment) }
        return gPayment
    }
}
