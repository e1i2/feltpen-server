package io.github.e1i2.workspace.controller

import io.github.e1i2.workspace.service.InvitationTarget
import io.github.e1i2.workspace.service.WorkspaceListResponse
import io.github.e1i2.workspace.service.WorkspaceMemberDto
import io.github.e1i2.workspace.service.WorkspaceResponse
import io.github.e1i2.workspace.service.WorkspaceService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class WorkspaceController(
    private val workspaceService: WorkspaceService
) {
    @PostMapping("/api-public/feltpen/workspaces")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun saveWorkspace(@RequestBody @Valid request: CreateWorkspaceRequest): CreateWorkspaceResponse {
        val result = workspaceService.saveWorkspace(request.workspaceName)
        return CreateWorkspaceResponse(result)
    }
    @PostMapping("/api-public/feltpen/workspaces/{workspaceId}/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun inviteToWorkspace(@PathVariable workspaceId: Long, @RequestBody @Valid request: InvitationWorkspaceRequest) {
        workspaceService.sendWorkspaceInvitation(workspaceId, request.targets)
    }

    @GetMapping("/api-public/feltpen/workspaces/{workspaceId}/invitations")
    suspend fun getInvitedUsers(@PathVariable workspaceId: Long): WorkspaceInvitationListResponse {
        val workspaceInvitationResponses = workspaceService.getInvitedUsers(workspaceId)
            .map {
                WorkspaceInvitationResponse(
                    invitationId = it.id,
                    email = it.email,
                    role =  it.role.toString()
                )
            }

        return WorkspaceInvitationListResponse(workspaceInvitationResponses)
    }

    @DeleteMapping("/api-public/feltpen/workspaces/invitations/{invitationId}")
    suspend fun deleteInvite(@PathVariable invitationId: Long) {
        workspaceService.deleteInvitedUser(invitationId)
    }

    @PostMapping("/api-public/feltpen/workspaces/{workspaceId}/join")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun joinToWorkspace(@PathVariable workspaceId: Long, @RequestBody @Valid request: JoinWorkspaceRequest) {
        workspaceService.joinToWorkspace(workspaceId, request.code)
    }

    @GetMapping("/api-public/feltpen/workspaces/list")
    suspend fun getAllJoinedWorkspace(): WorkspaceListResponse {
        return workspaceService.getAllJoinedWorkspace()
    }

    @GetMapping("/api-public/feltpen/workspaces/{workspaceId}")
    suspend fun getWorkspaceById(@PathVariable("workspaceId") workspaceId: Long): WorkspaceResponse {
        return workspaceService.getWorkspaceById(workspaceId)
    }

    @GetMapping("/api-public/feltpen/workspaces/{workspaceId}/members")
    suspend fun getAllWorkspaceMembers(@PathVariable("workspaceId") workspaceId: Long): WorkspaceMemberListResponse {
        return WorkspaceMemberListResponse(workspaceService.getAllWorkspaceMember(workspaceId))
    }
}

data class CreateWorkspaceRequest(
    @NotBlank
    val workspaceName: String
)

data class CreateWorkspaceResponse(
    val workspaceId: Long
)

data class InvitationWorkspaceRequest(
    @NotNull
    val targets: List<InvitationTarget>
)

data class WorkspaceInvitationListResponse(
    val invitations: List<WorkspaceInvitationResponse>
)
data class WorkspaceInvitationResponse(
    val invitationId: Long,
    val email: String,
    val role: String
)

data class JoinWorkspaceRequest(
    @NotBlank
    val code: String
)

data class WorkspaceMemberListResponse(
    val users: List<WorkspaceMemberDto>
)