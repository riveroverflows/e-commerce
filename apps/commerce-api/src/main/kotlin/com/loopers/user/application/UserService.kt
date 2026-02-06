package com.loopers.user.application

import com.loopers.user.application.model.UserSignUpCommand
import com.loopers.user.domain.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun signUp(command: UserSignUpCommand): UserInfo {
        TODO("Step 2 Green에서 구현")
    }
}
