package io.github.e1i2.user.service

import io.github.e1i2.global.security.authentication.AuthenticationService
import io.github.e1i2.global.security.jwt.TokenGenerator
import io.github.e1i2.global.adapter.MailSender
import io.github.e1i2.user.User
import io.github.e1i2.user.repository.UserRepository
import io.github.e1i2.user.verificationcode.VerificationCode
import io.github.e1i2.user.verificationcode.repository.VerificationCodeRepository
import java.time.LocalDateTime
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class UserService(
    private val verificationCodeRepository: VerificationCodeRepository,
    private val tokenGenerator: TokenGenerator,
    private val mailSender: MailSender,
    private val userRepository: UserRepository,
    private val authenticationService: AuthenticationService
) {
    suspend fun sendVerificationCode(email: String) {
        // TODO VerificaitonCode가 이미 있는 경우 update 방식을 사용해야 한다
        val verificationCode = saveVerificationCode(email)
        mailSender.sendEmailAsync("인증 코드", verificationCode.code, email)
    }

    private suspend fun saveVerificationCode(email: String): VerificationCode {
        val verificationCode = VerificationCode(email = email)
        return verificationCodeRepository.save(verificationCode)
    }

    suspend fun signIn(email: String, code: String): TokenDto {
        val verificationCode = getOrThrowOnInvalidCode(email, code)
        val user = userRepository.findByEmail(email)
            ?: userRepository.save(User(email = email))
        val usedVerificationCode = verificationCode.markAsUsed()
        verificationCodeRepository.save(usedVerificationCode)
        return generateTokens(subject = user.id.toString())
    }

    private suspend fun getOrThrowOnInvalidCode(email: String, code: String): VerificationCode {
        val verificationCode = verificationCodeRepository.findVerificationCodeByEmailAndCodeAndIsUsedFalse(email, code)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Verification code not found")

        if (verificationCode.isExpired()) {
            throw ResponseStatusException(HttpStatus.GONE, "Expired verification code")
        }
        return verificationCode
    }

    private fun generateTokens(subject: String): TokenDto {
        val accessTokenExpireAt = LocalDateTime.now().plusDays(14)
        val accessToken = tokenGenerator.generate(subject, accessTokenExpireAt)

        return TokenDto(
            accessToken = accessToken,
            accessTokenExpireAt = accessTokenExpireAt,
        )
    }

    suspend fun getCurrentUserInfo(): UserInfo {
        val userId = authenticationService.currentUserId() ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        val user = userRepository.findById(userId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        return UserInfo(
            userId = user.id,
            name =  user.name,
            email =  user.email
        )
    }
}

data class TokenDto(
    val accessToken: String,
    val accessTokenExpireAt: LocalDateTime,
)

data class UserInfo(
    val userId: Long,
    val name: String,
    val email: String
)