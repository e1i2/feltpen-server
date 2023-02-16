package io.github.e1i2.user

import java.time.LocalDateTime

object TestUtils {
    fun buildUser(
        name: String = "name",
        email: String = "email@gmail.com"
    ) = User(
        name = name,
        email = email,
        createdAt = LocalDateTime.now()
    )
}