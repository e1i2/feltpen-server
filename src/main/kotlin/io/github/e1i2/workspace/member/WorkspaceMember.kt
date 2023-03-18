package io.github.e1i2.workspace.member

import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("workspace_member")
data class WorkspaceMember(
    @Id
    val id: Long = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null,
    val userId: Long,
    val workspaceId: Long,
    val role: Role
)

enum class Role {
    OWNER,
    MEMBER
}
