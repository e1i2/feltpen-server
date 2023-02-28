package io.github.e1i2.user.service

import com.ninjasquad.springmockk.MockkBean
import io.github.e1i2.global.security.jwt.TokenGenerator
import io.github.e1i2.user.TestUtils.buildUser
import io.github.e1i2.user.adapter.MailSender
import io.github.e1i2.user.verificationcode.VerificationCode
import io.github.e1i2.user.verificationcode.repository.VerificationCodeRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class UserServiceTest(
    @MockkBean
    private val verificationCodeRepository: VerificationCodeRepository = mockk(),
    @MockkBean
    private val mailSender: MailSender = mockk(),
    @MockkBean
    private val tokenGenerator: TokenGenerator = mockk(),
    private val userService: UserService = UserService(
        mailSender = mailSender,
        tokenGenerator = tokenGenerator,
        verificationCodeRepository = verificationCodeRepository
    )
) : StringSpec() {
    private val user = buildUser()

    init {
        val verificationCode = VerificationCode(email = "email", code = "code", createdAt = LocalDateTime.now())
        val expiredVerificationCode = VerificationCode(email = "email", code = "code", createdAt = LocalDateTime.now().minusMinutes(40))

        "인증 코드 전송 테스트" {
            // given
            coEvery { verificationCodeRepository.save(any()) } coAnswers { verificationCode }
            coEvery { mailSender.sendEmailAsync(any(), any(), user.email) } coAnswers { }

            // when
            userService.sendVerificationCode(email = user.email)

            // then
            coVerify(exactly = 1) { verificationCodeRepository.save(any()) }
        }

        "로그인 테스트" {
            // given
            coEvery { verificationCodeRepository.save(any()) } coAnswers { verificationCode }
            coEvery { tokenGenerator.generate(any(), any()) } coAnswers { "token" }
            coEvery { verificationCodeRepository.save(any()) } coAnswers { verificationCode }
            coEvery {
                verificationCodeRepository.findVerificationCodeByEmailAndCodeAndIsUsedFalse(verificationCode.email, verificationCode.code)
            } coAnswers { verificationCode }

            // when
            val tokenDto = userService.signIn(verificationCode.email, verificationCode.code)

            // then
            coVerify(exactly = 1) { tokenGenerator.generate(any(), any()) }
            coVerify(exactly = 1) { verificationCodeRepository.save(any()) }
            tokenDto.accessToken shouldBe "token"
            tokenDto.accessTokenExpireAt shouldBeAfter LocalDateTime.now()
        }

        "로그인 - 만료된 코드 테스트" {
            // given
            coEvery {
                verificationCodeRepository.findVerificationCodeByEmailAndCodeAndIsUsedFalse(expiredVerificationCode.email, expiredVerificationCode.code)
            } coAnswers { expiredVerificationCode }

            // when
            val exception = shouldThrow<ResponseStatusException> {
                userService.signIn(expiredVerificationCode.email, expiredVerificationCode.code)
            }

            // then
            exception.statusCode shouldBe HttpStatus.GONE
        }

        "로그인 - 찾을 수 없는 코드" {
            // given
            coEvery {
                verificationCodeRepository.findVerificationCodeByEmailAndCodeAndIsUsedFalse(any(), any())
            } coAnswers { null }

            // when
            val exception = shouldThrow<ResponseStatusException> {
                userService.signIn("email", "code")
            }

            // then
            exception.statusCode shouldBe HttpStatus.NOT_FOUND
        }
    }
}
