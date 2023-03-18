package io.github.e1i2.workspace.controller

import io.github.e1i2.workspace.invitation.WorkspaceInvitation
import io.github.e1i2.workspace.service.InvitationTarget
import io.github.e1i2.workspace.service.WorkspaceListResponse
import io.github.e1i2.workspace.service.WorkspaceMemberDto
import io.github.e1i2.workspace.service.WorkspaceResponse
import io.github.e1i2.workspace.service.WorkspaceService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/workspaces")
class WorkspaceController(
    private val workspaceService: WorkspaceService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun saveWorkspace(@RequestBody @Valid request: CreateWorkspaceRequest): CreateWorkspaceResponse {
        val result = workspaceService.saveWorkspace(request.workspaceName)
        return CreateWorkspaceResponse(result)
    }

    @PostMapping("/{workspaceId}/invites")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun inviteToWorkspace(@PathVariable workspaceId: Long, @RequestBody @Valid request: InviteWorkspaceRequest) {
        workspaceService.sendWorkspaceInvitation(workspaceId, request.targets)
    }

    @GetMapping("/{workspaceId}/invites")
    suspend fun getInvitedUsers(@PathVariable workspaceId: Long): WorkspaceInvitationListResponse {
        val workspaceInvitationResponses = workspaceService.getInvitedUsers(workspaceId)
            .map {
                WorkspaceInvitationResponse(
                    invitationId = it.id,
                    email = it.email,
                )
            }

        return WorkspaceInvitationListResponse(workspaceInvitationResponses)
    }

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun joinToWorkspace(@RequestBody @Valid request: JoinWorkspaceRequest) {
        workspaceService.joinToWorkspace(request.workspaceId, request.code)
    }

    @GetMapping("/list")
    suspend fun getAllJoinedWorkspace(): WorkspaceListResponse {
        return workspaceService.getAllJoinedWorkspace()
    }

    @GetMapping("/{workspaceId}")
    suspend fun getWorkspaceById(@PathVariable("workspaceId") workspaceId: Long): WorkspaceResponse {
        return workspaceService.getWorkspaceById(workspaceId)
    }

    @GetMapping("/{workspaceId}/members")
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

data class InviteWorkspaceRequest(
    @NotNull
    val targets: List<InvitationTarget>
)

data class WorkspaceInvitationListResponse(
    val invitations: List<WorkspaceInvitationResponse>
)
data class WorkspaceInvitationResponse(
    val invitationId: Long,
    val email: String
)

data class JoinWorkspaceRequest(
    @NotNull
    val workspaceId: Long,
    @NotBlank
    val code: String
)

data class WorkspaceMemberListResponse(
    val users: List<WorkspaceMemberDto>
)