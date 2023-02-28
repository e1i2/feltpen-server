package io.github.e1i2.user.service

import io.github.e1i2.global.security.jwt.TokenGenerator
import io.github.e1i2.user.adapter.MailSender
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
    private val mailSender: MailSender
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
        val usedVerificationCode = verificationCode.markAsUsed()
        verificationCodeRepository.save(usedVerificationCode)
        return generateTokens(subject = email)
    }

    private suspend fun getOrThrowOnInvalidCode(email: String, code: String): VerificationCode {
        val verificationCode = verificationCodeRepository.findVerificationCodeByEmailAndCodeAndUsedFalse(email, code)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Verification code not found")

        if (verificationCode.isExpired()) {
            throw ResponseStatusException(HttpStatus.GONE, "Expired verification code")
        }
        return verificationCode
    }

    private fun generateTokens(subject: String): TokenDto {
        val accessTokenExpireAt = LocalDateTime.now().plusHours(2)
        val refreshTokenExpireAt = LocalDateTime.now().plusDays(14)

        val accessToken = tokenGenerator.generate(subject, accessTokenExpireAt)
        val refreshToken = tokenGenerator.generate(subject, refreshTokenExpireAt)

        return TokenDto(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpireAt = accessTokenExpireAt,
            refreshTokenExpireAt = refreshTokenExpireAt
        )
    }
}

data class TokenDto(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpireAt: LocalDateTime,
    val refreshTokenExpireAt: LocalDateTime
)
