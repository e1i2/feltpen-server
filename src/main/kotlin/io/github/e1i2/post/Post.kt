package io.github.e1i2.post

import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("post")
data class Post(
    @Id
    val id: Long = 0,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null,
    val title: String,
    val writerId: Long,
    val workspaceId: Long,
    val status: Status
)

enum class Status {
    PENDING,
    COMPLETE
}
