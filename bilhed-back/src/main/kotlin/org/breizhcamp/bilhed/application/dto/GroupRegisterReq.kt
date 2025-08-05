package org.breizhcamp.bilhed.application.dto

import org.breizhcamp.bilhed.domain.entities.PassType

data class GroupRegisterReq(
    val referent: PersonRegisterReq,
    val groupPayment: Boolean,
    val companions: List<PersonRegisterReq>,
    val pass: PassType
) {
    fun validate(groupPayment: Boolean): Boolean {
        // GroupPayment is TRUE for a single Person
        val gPayment = if (companions.isEmpty()) true else groupPayment
        referent.validate(gPayment, true)
        companions.forEach { it.validate(gPayment) }
        return gPayment
    }
}
