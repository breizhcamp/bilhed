package org.breizhcamp.bilhed.application.dto

import org.breizhcamp.bilhed.domain.entities.PassType

class ReferentRegisterReq(
    lastname: String,
    firstname: String,
    email: String,
    telephone: String?,
    val pass: PassType,
    kids: String?,
): CompanionRegisterReq(lastname, firstname, telephone, email, kids)
