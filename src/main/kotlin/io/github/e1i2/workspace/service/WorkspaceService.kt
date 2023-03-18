package io.github.e1i2.workspace.service

import io.github.e1i2.global.security.authentication.AuthenticationService
import io.github.e1i2.global.adapter.MailSender
import io.github.e1i2.utils.getRandomString
import io.github.e1i2.workspace.Workspace
import io.github.e1i2.workspace.invitation.WorkspaceInvitation
import io.github.e1i2.workspace.invitation.repository.WorkspaceInvitationRepository
import io.github.e1i2.workspace.member.Role
import io.github.e1i2.workspace.member.WorkspaceMember
import io.github.e1i2.workspace.member.repository.WorkspaceMemberRepository
import io.github.e1i2.workspace.repository.WorkspaceRepository
import java.time.LocalDateTime
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import org.springframework.web.server.ResponseStatusException

@Service
class WorkspaceService(
    private val workspaceRepository: WorkspaceRepository,
    private val workspaceInvitationRepository: WorkspaceInvitationRepository,
    private val workspaceMemberRepository: WorkspaceMemberRepository,
    private val authenticationService: AuthenticationService,
    private val transactionalOperator: TransactionalOperator,
    private val mailSender: MailSender
) {
    suspend fun saveWorkspace(name: String) = transactionalOperator.executeAndAwait {
        val creatorId = authenticationService.currentUserIdOrThrow()

        val workspace = Workspace(name = name)
        val savedWorkspace = workspaceRepository.save(workspace)

        val creatorMember = WorkspaceMember(
            workspaceId = savedWorkspace.id,
            userId = creatorId,
            role = Role.OWNER
        )
        workspaceMemberRepository.save(creatorMember)

        savedWorkspace.id
    }!!

    suspend fun sendWorkspaceInvitation(workspaceId: Long, emails: List<String>) {
        val userId = authenticationService.currentUserIdOrThrow()
        if (!isWorkspaceMember(workspaceId, userId)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden")
        }
        sendInvitationCodeToEmails(workspaceId, emails)
    }

    private suspend fun sendInvitationCodeToEmails(workspaceId: Long, emails: List<String>) {
        emails.forEach {
            val invitationCode = getRandomString(20)

            workspaceInvitationRepository.save(
                WorkspaceInvitation(
                    workspaceId = workspaceId,
                    email = it,
                    code = invitationCode,
                    expireAt = LocalDateTime.now().plusHours(2)
                )
            )
            mailSender.sendEmailAsync("Workspace invitation", "localhost:3000/workspace/$workspaceId/join?code=$invitationCode", it)
        }
    }

    suspend fun joinToWorkspace(workspaceId: Long, code: String) {
        val currentUserId = authenticationService.currentUserIdOrThrow()
        val invitation = workspaceInvitationRepository.findByWorkspaceIdAndCode(workspaceId, code)
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid invitation code")
        invitation.checkIsExpired()

        workspaceMemberRepository.save(
            WorkspaceMember(
                workspaceId = workspaceId,
                userId = currentUserId,
                role = Role.MEMBER
            )
        )
    }

    suspend fun getAllJoinedWorkspace(): WorkspaceListResponse {
        val currentUserId = authenticationService.currentUserIdOrThrow()
        val workspaces = workspaceRepository.findAllByWorkspaceMemberId(currentUserId)
        val workspaceResponses = workspaces.map { it.toResponse() }
        return WorkspaceListResponse(workspaceResponses)
    }

    suspend fun getWorkspaceById(workspaceId: Long): WorkspaceResponse {
        val currentUserId = authenticationService.currentUserIdOrThrow()
        checkIsWorkspaceMember(workspaceId, currentUserId)
        val workspace = workspaceRepository.findById(workspaceId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found")
        return workspace.toResponse()
    }

    private fun Workspace.toResponse() =
        WorkspaceResponse(
            id = id,
            name = name,
            createdAt = createdAt
        )

    suspend fun getAllWorkspaceMember(workspaceId: Long): WorkspaceMemberListResponse {
        val currentUserId = authenticationService.currentUserIdOrThrow()
        checkIsWorkspaceMember(workspaceId, currentUserId)
        val workspaceMemberResponses = workspaceMemberRepository.findAllMemberByWorkspaceId(workspaceId)
        return WorkspaceMemberListResponse(workspaceMemberResponses)
    }

    private suspend fun checkIsWorkspaceMember(workspaceId: Long, userId: Long) {
        if (!isWorkspaceMember(workspaceId, userId)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "not member of workspace")
        }
    }

    private suspend fun isWorkspaceMember(workspaceId: Long, userId: Long): Boolean {
        return workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId) != null
    }
}

data class WorkspaceListResponse(
    val workspaces: List<WorkspaceResponse>
)

data class WorkspaceResponse(
    val id: Long,
    val name: String,
    val createdAt: LocalDateTime
)

data class WorkspaceMemberListResponse(
    val users: List<WorkspaceMemberResponse>
)

data class WorkspaceMemberResponse(
    val userId: Long,
    val memberId: Long,
    val name: String,
    val profileImage: String,
    val role: String
)
