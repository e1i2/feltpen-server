package io.github.e1i2.user.controller

import io.github.e1i2.user.service.UserService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.http.HttpStatus
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
}

data class SendVerificationCodeRequest(
    @field:Email
    val email: String
)
