package io.github.e1i2.user.service

import com.ninjasquad.springmockk.MockkBean
import io.github.e1i2.user.TestUtils.buildUser
import io.github.e1i2.user.adapter.MailSender
import io.github.e1i2.user.verificationcode.VerificationCode
import io.github.e1i2.user.verificationcode.repository.VerificationCodeRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest

@WebFluxTest(UserService::class)
class UserServiceTest(
    private val userService: UserService,
    @MockkBean
    private val verificationCodeRepository: VerificationCodeRepository = mockk(),
    @MockkBean
    private val mailSender: MailSender = mockk()
) : StringSpec() {
    private val user = buildUser()

    init {

        "사용자가 없는 경우 새로 생성한다" {
            // given
            coEvery { verificationCodeRepository.save(any()) } coAnswers { VerificationCode(email = "email", code = "code", expireAt = LocalDateTime.now()) }
            coEvery { mailSender.sendEmailAsync(any(), any(), user.email) } coAnswers { }

            // when
            userService.sendVerificationCode(email = user.email)

            // then
            coVerify(exactly = 1) { verificationCodeRepository.save(any()) }
        }

        "사용자가 있는 경우 생성되지 않는다" {
            // given
            coEvery { verificationCodeRepository.save(any()) } coAnswers { VerificationCode(email = "email", code = "code", expireAt = LocalDateTime.now()) }
            coEvery { mailSender.sendEmailAsync(any(), any(), user.email) } coAnswers { }

            // when
            userService.sendVerificationCode(email = user.email)

            // then
            coVerify(exactly = 1) { verificationCodeRepository.save(any()) } }
    }
}
