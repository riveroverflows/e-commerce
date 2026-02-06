package com.loopers.user.interfaces.api

import java.time.LocalDate

class UserV1Dto {
    data class SignUpRequest(
        val loginId: String,
        val password: String,
        val name: String,
        val birthDate: LocalDate,
        val email: String,
    )

    data class SignUpResponse(
        val loginId: String,
    )
}
