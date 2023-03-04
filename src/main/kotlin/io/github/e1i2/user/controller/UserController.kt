package io.github.e1i2.user.controller

import io.github.e1i2.user.service.TokenDto
import io.github.e1i2.user.service.UserInfo
import io.github.e1i2.user.service.UserService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/verification-code")
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun sendVerificationCode(@RequestBody @Valid request: SendVerificationCodeRequest) {
        userService.sendVerificationCode(request.email)
    }

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun signIn(@RequestBody @Valid signInRequest: SignInRequest): TokenDto {
        return userService.signIn(signInRequest.email, signInRequest.code)
    }

    @GetMapping
    suspend fun getCurrentUserInfo(): UserInfo {
        return userService.getCurrentUserInfo()
    }
}

data class SendVerificationCodeRequest(
    @field:Email
    val email: String
)

data class SignInRequest(
    @field:Email
    val email: String,
    @field:NotBlank
    val code: String
)
