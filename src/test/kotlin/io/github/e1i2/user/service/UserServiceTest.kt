package io.github.e1i2.user.service

import com.ninjasquad.springmockk.MockkBean
import io.github.e1i2.user.TestUtils.buildUser
import io.github.e1i2.user.adapter.MailSender
import io.github.e1i2.user.repository.UserRepository
import io.github.e1i2.user.verificationcode.VerificationCode
import io.github.e1i2.user.verificationcode.repository.VerificationCodeRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.mail.javamail.JavaMailSender

@WebFluxTest(UserService::class)
class UserServiceTest(
    private val userService: UserService,
    @MockkBean
    private val verificationCodeRepository: VerificationCodeRepository = mockk(),
    @MockkBean
    private val userRepository: UserRepository = mockk(),
    @MockkBean
    private val mailSender: MailSender = mockk()
) : StringSpec() {
    private val user = buildUser()

    init {

        "사용자가 없는 경우 새로 생성한다" {
            // given
            coEvery { verificationCodeRepository.save(any()) } coAnswers { VerificationCode(email = "email", code = "code", expireAt = LocalDateTime.now()) }
            coEvery { userRepository.findByEmail(user.email) } coAnswers { null }
            coEvery { userRepository.save(any()) } coAnswers { user }
            coEvery { mailSender.sendEmailAsync(any(), any(), user.email) } coAnswers { }

            // when
            userService.signIn(email = user.email)

            // then
            coVerify(exactly = 1) { userRepository.save(any()) }
            coVerify(exactly = 1) { userRepository.findByEmail(user.email) }
            coVerify(exactly = 1) { verificationCodeRepository.save(any()) }
        }

        "사용자가 있는 경우 생성되지 않는다" {
            // given
            coEvery { verificationCodeRepository.save(any()) } coAnswers { VerificationCode(email = "email", code = "code", expireAt = LocalDateTime.now()) }
            coEvery { userRepository.findByEmail(user.email) } coAnswers { user }
            coEvery { mailSender.sendEmailAsync(any(), any(), user.email) } coAnswers { }

            // when
            userService.signIn(email = user.email)

            // then
            coVerify(exactly = 0) { userRepository.save(any()) }
            coVerify(exactly = 1) { verificationCodeRepository.save(any()) } }
    }
}
