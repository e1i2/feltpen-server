package io.github.e1i2.post

import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("post")
data class Post(
    @Id
    val id: Long = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val status: Status,
    val folderId: Long
)

enum class Status {
    DRAFT,
    PUBLISHED
}
