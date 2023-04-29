package io.github.e1i2.post

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("post_data")
data class PostData(
    @Id
    val id: Long = 0,
    val writerId: Long,
    val postId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null,
    val title: String,
    val content: JsonNode
)
