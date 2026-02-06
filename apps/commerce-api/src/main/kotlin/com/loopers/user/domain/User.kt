package com.loopers.user.domain

import java.time.LocalDate

class User private constructor(
    val loginId: String,
    val password: String,
    val name: String,
    val birthDate: LocalDate,
    val email: String,
) {
    companion object {
        fun register(
            loginId: String,
            password: String,
            name: String,
            birthDate: LocalDate,
            email: String,
        ): User {
            TODO("Step 3에서 구현")
        }
    }
}
