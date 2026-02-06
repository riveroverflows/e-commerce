package com.loopers.user.application

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.user.application.model.UserSignUpCommand
import com.loopers.user.domain.User
import com.loopers.user.domain.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate

@DisplayName("UserService")
class UserServiceTest {
    private val userRepository: UserRepository = mock()
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
    private val userService = UserService(userRepository, passwordEncoder)

    private fun createCommand(
        loginId: String = "testuser1",
        password: String = "Password1!",
        name: String = "홍길동",
        birthDate: LocalDate = LocalDate.of(1990, 1, 1),
        email: String = "test@example.com",
    ): UserSignUpCommand = UserSignUpCommand(loginId, password, name, birthDate, email)

    @Nested
    @DisplayName("회원가입")
    inner class SignUp {
        @Test
        @DisplayName("회원가입 성공 - UserInfo 반환")
        fun signUp_success_returnsUserInfo() {
            // arrange
            val command = createCommand()
            given(userRepository.existsByLoginId(command.loginId)).willReturn(false)
            given(userRepository.save(any())).willAnswer { it.arguments[0] as User }

            // act
            val result = userService.signUp(command)

            // assert
            assertThat(result.loginId).isEqualTo("testuser1")
        }

        @Test
        @DisplayName("회원가입 성공 - 비밀번호가 BCrypt로 암호화됨")
        fun signUp_success_passwordIsEncrypted() {
            // arrange
            val command = createCommand()
            given(userRepository.existsByLoginId(command.loginId)).willReturn(false)
            given(userRepository.save(any())).willAnswer { it.arguments[0] as User }

            // act
            userService.signUp(command)

            // assert
            then(userRepository).should().save(
                org.mockito.kotlin.check { user ->
                    assertThat(user.password).isNotEqualTo("Password1!")
                    assertThat(passwordEncoder.matches("Password1!", user.password)).isTrue
                },
            )
        }

        @Test
        @DisplayName("회원가입 성공 - Repository.save() 호출됨")
        fun signUp_success_callsRepositorySave() {
            // arrange
            val command = createCommand()
            given(userRepository.existsByLoginId(command.loginId)).willReturn(false)
            given(userRepository.save(any())).willAnswer { it.arguments[0] as User }

            // act
            userService.signUp(command)

            // assert
            then(userRepository).should().save(any())
        }

        @Test
        @DisplayName("회원가입 실패 - 중복 loginId - CoreException(USER_DUPLICATE_LOGIN_ID)")
        fun signUp_duplicateLoginId_throwsException() {
            // arrange
            val command = createCommand()
            given(userRepository.existsByLoginId(command.loginId)).willReturn(true)

            // act
            val exception = assertThrows<CoreException> {
                userService.signUp(command)
            }

            // assert
            assertThat(exception.errorType).isEqualTo(ErrorType.USER_DUPLICATE_LOGIN_ID)
        }
    }

    @Nested
    @DisplayName("내 정보 조회")
    inner class GetMe {
        @Test
        @DisplayName("유효한 인증 정보로 조회 시 UserInfo를 반환한다")
        fun getMe_success_returnsUserInfo() {
            // arrange
            val user = User.retrieve(
                loginId = "testuser1",
                password = passwordEncoder.encode("Password1!"),
                name = "홍길동",
                birthDate = LocalDate.of(1990, 1, 1),
                email = "test@example.com",
            )
            given(userRepository.findByLoginId("testuser1")).willReturn(user)

            // act
            val result = userService.getMe("testuser1", "Password1!")

            // assert
            assertAll(
                { assertThat(result.loginId).isEqualTo("testuser1") },
                { assertThat(result.name).isEqualTo("홍길*") },
                { assertThat(result.birthDate).isEqualTo(LocalDate.of(1990, 1, 1)) },
                { assertThat(result.email).isEqualTo("test@example.com") },
            )
        }

        @Test
        @DisplayName("존재하지 않는 loginId로 조회 시 CoreException(UNAUTHORIZED)")
        fun getMe_invalidLoginId_throwsException() {
            // arrange
            given(userRepository.findByLoginId("nonexistent")).willReturn(null)

            // act
            val exception = assertThrows<CoreException> {
                userService.getMe("nonexistent", "Password1!")
            }

            // assert
            assertThat(exception.errorType).isEqualTo(ErrorType.UNAUTHORIZED)
        }

        @Test
        @DisplayName("비밀번호 불일치 시 CoreException(UNAUTHORIZED)")
        fun getMe_wrongPassword_throwsException() {
            // arrange
            val user = User.retrieve(
                loginId = "testuser1",
                password = passwordEncoder.encode("Password1!"),
                name = "홍길동",
                birthDate = LocalDate.of(1990, 1, 1),
                email = "test@example.com",
            )
            given(userRepository.findByLoginId("testuser1")).willReturn(user)

            // act
            val exception = assertThrows<CoreException> {
                userService.getMe("testuser1", "WrongPassword1!")
            }

            // assert
            assertThat(exception.errorType).isEqualTo(ErrorType.UNAUTHORIZED)
        }
    }
}
