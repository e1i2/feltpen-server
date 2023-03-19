package io.github.e1i2.post

import com.fasterxml.jackson.databind.JsonNode
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
    val status: Status,
    val content: JsonNode
) {
    fun getUpdatedPost(title: String, content: JsonNode): Post {
        return copy(
            title = title,
            content = content,
            updatedAt = LocalDateTime.now()
        )
    }
}

enum class Status {
    DRAFT,
    PUBLISHED
}
