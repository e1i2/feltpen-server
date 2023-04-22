package io.github.e1i2.post.block

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("block")
data class Block(
    @Id
    val id: Long = 0,
    val content: JsonNode,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val order: Long,
    val postId: Long
)
