package com.loopers.user.interfaces.api

import com.loopers.user.application.UserInfo
import com.loopers.user.application.model.UserSignUpCommand
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

class UserV1Dto {
    data class SignUpRequest(
        @field:NotBlank
        val loginId: String,
        val password: String,
        val name: String,
        val birthDate: LocalDate,
        val email: String,
    ) {
        fun toCommand(): UserSignUpCommand =
            UserSignUpCommand(
                loginId = loginId,
                password = password,
                name = name,
                birthDate = birthDate,
                email = email,
            )
    }

    data class SignUpResponse(
        val loginId: String,
    ) {
        companion object {
            fun from(info: UserInfo): SignUpResponse =
                SignUpResponse(
                    loginId = info.loginId,
                )
        }
    }
}
