package io.github.e1i2.global.security.authentication

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldNotContain

class TokenAuthenticationTest: StringSpec() {
    init {
        "Bearer를 빼내올 수 있어야 한다" {
            val upperCaseStartedJwtToken = TokenAuthentication("", "Bearer testToken").jwtToken
            upperCaseStartedJwtToken shouldNotContain "Bearer "

            val lowerCaseStartedJwtToken = TokenAuthentication("", "bearer testToken").jwtToken
            lowerCaseStartedJwtToken shouldNotContain "bearer "
        }
    }
}
