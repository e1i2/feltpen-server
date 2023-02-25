package io.github.e1i2.user.controller

import com.ninjasquad.springmockk.MockkBean
import io.github.e1i2.global.security.SpringSecurityConfig
import io.github.e1i2.user.TestUtils.buildUser
import io.github.e1i2.user.service.UserService
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec

@WebFluxTest(UserController::class, SpringSecurityConfig::class)
class UserControllerTest(
    private val webTestClient: WebTestClient,
    @MockkBean
    private val userService: UserService = mockk()
) : StringSpec() {
    init {
        "요청에 문제가 없을 경우 201이 반환된다" {
            coEvery { userService.sendVerificationCode(any()) } coAnswers { buildUser() }

            sendRequest(buildSendVerificationCodeRequest())
                .expectStatus().isAccepted

            coVerify(exactly = 1) { userService.sendVerificationCode(any()) }
        }

        "요청에 이상이 있을 시 400이 반환된다" {
            val request = buildSendVerificationCodeRequest(email = "hello")

            sendRequest(request)
                .expectStatus().isBadRequest
        }
    }

    private fun buildSendVerificationCodeRequest(
        email: String = "test@gmail.com"
    ): SendVerificationCodeRequest = SendVerificationCodeRequest(
        email = email
    )

    private fun sendRequest(request: SendVerificationCodeRequest): ResponseSpec {
        return webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/users/verification-code")
            .bodyValue(request)
            .exchange()
    }
}
