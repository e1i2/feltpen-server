package io.github.e1i2.user.service

import io.github.e1i2.user.User
import io.github.e1i2.user.adapter.MailSender
import io.github.e1i2.user.repository.UserRepository
import io.github.e1i2.user.verificationcode.VerificationCode
import io.github.e1i2.user.verificationcode.repository.VerificationCodeRepository
import org.springframework.stereotype.Component

@Component
class UserService(
    private val verificationCodeRepository: VerificationCodeRepository,
    private val mailSender: MailSender
) {
    suspend fun sendVerificationCode(email: String) {
        val verificationCode = saveVerificationCode(email)
        mailSender.sendEmailAsync("인증 코드", verificationCode.code, email)
    }

    private suspend fun saveVerificationCode(email: String): VerificationCode {
        val verificationCode = VerificationCode(email = email)
        return verificationCodeRepository.save(verificationCode)
    }
}
