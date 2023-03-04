package io.github.e1i2.workspace.invitation

import io.github.e1i2.utils.getRandomString
import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("workspace_invitation")
data class WorkspaceInvitation(
    @Id
    val id: Long = 0,
    val code: String = getRandomString(15),
    val email: String,
    val workspaceId: Long,
    val expireAt: LocalDateTime = LocalDateTime.now().plusDays(1)
)
