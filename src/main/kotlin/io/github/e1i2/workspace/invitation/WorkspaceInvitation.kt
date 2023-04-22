package io.github.e1i2.workspace.invitation

import io.github.e1i2.workspace.member.Role
import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@Table("workspace_invitation")
data class WorkspaceInvitation(
    @Id
    val id: Long = 0,
    val code: String,
    val email: String,
    val workspaceId: Long,
    val expireAt: LocalDateTime,
    val role: Role
) {
    fun checkIsExpired() {
        if (expireAt.isBefore(LocalDateTime.now())) {
            throw ResponseStatusException(HttpStatus.GONE, "Expired verification code")
        }
    }
}
