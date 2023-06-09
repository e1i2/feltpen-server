package io.github.e1i2.user.controller

import io.github.e1i2.user.service.TokenDto
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
class UserController(
    private val userService: UserService
) {
    @PostMapping("/api-public/feltpen/users/verification-code")
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun sendVerificationCode(@RequestBody @Valid request: SendVerificationCodeRequest) {
        userService.sendVerificationCode(request.email)
    }

    @PostMapping("/api-public/feltpen/users/signin")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun signIn(@RequestBody @Valid signInRequest: SignInRequest): TokenDto {
        return userService.signIn(signInRequest.email, signInRequest.code)
    }

    @GetMapping("/api-public/feltpen/users")
    suspend fun getCurrentUserInfo(): UserInfo {
        val user = userService.getCurrentUserInfo()
        return UserInfo(
            userId = user.id,
            email = user.email,
            profileImage = user.profileImage,
            name = user.name
        )
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

data class UserInfo(
    val userId: Long,
    val name: String,
    val email: String,
    val profileImage: String?
)
