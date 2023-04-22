package io.github.e1i2.workspace

import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("workspace")
data class Workspace(
    @Id
    val id: Long = 0,
    val name: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null,
    val profileImage: String? = null
)
