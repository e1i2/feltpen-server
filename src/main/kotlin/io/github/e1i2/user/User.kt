package io.github.e1i2.user

import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "user")
data class User(
    @Id
    val id: Long = 0,
    val email: String,
    val name: String = email.substringBeforeLast("@"),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null,
    val profileImage: String = "https://holyspiritchurch.us/wp-content/uploads/facebook-profile-blank-face.jpeg"
) {
    init {
        validateContents()
    }

    private fun validateContents() {
        assert(email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$".toRegex())) { "유효하지 않은 email입니다" }
        assert(name.length > 2) { "유효하지 않은 이름입니다" }
    }
}
