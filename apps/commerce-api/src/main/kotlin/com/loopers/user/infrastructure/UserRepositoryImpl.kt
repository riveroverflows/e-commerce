package com.loopers.user.infrastructure

import com.loopers.user.domain.User
import com.loopers.user.domain.UserRepository
import org.springframework.stereotype.Component

@Component
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    override fun save(user: User): User {
        TODO("Step 4 Green에서 구현")
    }

    override fun existsByLoginId(loginId: String): Boolean {
        TODO("Step 4 Green에서 구현")
    }
}
