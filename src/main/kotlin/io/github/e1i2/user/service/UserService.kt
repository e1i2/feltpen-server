package io.github.e1i2.user.service

import io.github.e1i2.user.User
import io.github.e1i2.user.adapter.MailSender
import io.github.e1i2.user.repository.UserRepository
import io.github.e1i2.user.verificationcode.VerificationCode
import io.github.e1i2.user.verificationcode.repository.VerificationCodeRepository
import org.springframework.stereotype.Component

@Component
class UserService(
    private val userRepository: UserRepository,
    private val verificationCodeRepository: VerificationCodeRepository,
    private val mailSender: MailSender
) {
    suspend fun signIn(email: String) {
        val user = userRepository.findByEmail(email) ?: saveNewUser(email)
        val verificationCode = saveVerificationCode(user)
        mailSender.sendEmailAsync("인증 코드", verificationCode.code, user.email)
    }

    private suspend fun saveNewUser(email: String): User {
        val newUser = User(
            email = email
        )

        return userRepository.save(newUser)
    }

    private suspend fun saveVerificationCode(user: User): VerificationCode {
        val verificationCode = VerificationCode(email = user.email)
        return verificationCodeRepository.save(verificationCode)
    }
}
