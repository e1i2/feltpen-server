package io.github.e1i2.workspace.invitation.controller

import io.github.e1i2.workspace.invitation.service.WorkspaceInvitationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class WorkspaceInvitationController(
    private val workspaceInvitationService: WorkspaceInvitationService
) {
    @PostMapping
    suspend fun sendInvitationToAll(@RequestBody request: WorkspaceInvitationRequest) {
        workspaceInvitationService.send(request.workspaceId, request.emails)
    }
}

data class WorkspaceInvitationRequest(
    val emails: List<String>,
    val workspaceId: Long
)