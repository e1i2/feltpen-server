package io.github.e1i2.workspace.service

import io.github.e1i2.global.security.authentication.AuthenticationService
import io.github.e1i2.global.adapter.MailSender
import io.github.e1i2.user.service.UserService
import io.github.e1i2.utils.getRandomString
import io.github.e1i2.workspace.Workspace
import io.github.e1i2.workspace.invitation.WorkspaceInvitation
import io.github.e1i2.workspace.invitation.repository.WorkspaceInvitationRepository
import io.github.e1i2.workspace.member.Role
import io.github.e1i2.workspace.member.WorkspaceMember
import io.github.e1i2.workspace.member.repository.WorkspaceMemberRepository
import io.github.e1i2.workspace.repository.WorkspaceRepository
import java.time.LocalDateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.dao.DuplicateKeyException
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
    private val userService: UserService,
    private val mailSender: MailSender
) {
    private val workspaceInvitationContent: String = String(ClassPathResource("workspace-invitation.html").inputStream.readAllBytes())

    suspend fun saveWorkspace(name: String) = transactionalOperator.executeAndAwait {
        val creator = userService.getCurrentUserInfo()

        val workspace = Workspace(name = name)
        val savedWorkspace = workspaceRepository.save(workspace)

        val creatorMember = WorkspaceMember(
            workspaceId = savedWorkspace.id,
            userId = creator.id,
            role = Role.OWNER,
            name = creator.name,
            profileImage = creator.profileImage
        )
        workspaceMemberRepository.save(creatorMember)

        savedWorkspace.id
    }!!

    suspend fun sendWorkspaceInvitation(workspaceId: Long, targets: List<InvitationTarget>) = transactionalOperator.executeAndAwait {
        val userId = authenticationService.currentUserIdOrThrow()
        checkIsWorkspaceMember(workspaceId, userId)
        sendInvitationCodeToEmails(workspaceId, targets)
    }

    private suspend fun sendInvitationCodeToEmails(workspaceId: Long, targets: List<InvitationTarget>) {
        targets.forEach {
            val invitationCode = getRandomString(20)

            runCatching {
                workspaceInvitationRepository.save(
                    WorkspaceInvitation(
                        workspaceId = workspaceId,
                        email = it.email,
                        code = invitationCode,
                        expireAt = LocalDateTime.now().plusHours(2),
                        role = it.role
                    )
                )
            }
            val content = workspaceInvitationContent.replace("{{ url }}", "https://app.feltpen.site/workspaces/$workspaceId/join?code=$invitationCode")

            mailSender.sendEmailAsync(
                "Workspace invitation",
                content,
                it.email
            )
        }
    }

    suspend fun getInvitedUsers(workspaceId: Long): List<WorkspaceInvitation> {
        val currentUserId = authenticationService.currentUserIdOrThrow()
        checkIsWorkspaceMember(workspaceId, currentUserId)
        return workspaceInvitationRepository.findAllByWorkspaceId(workspaceId)
    }

    suspend fun deleteInvitedUser(invitationId: Long) {
        val currentUserId = authenticationService.currentUserIdOrThrow()
        val invitation = workspaceInvitationRepository.findById(invitationId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace invitation not found")

        val workspaceMember = getWorkspaceMemberOrNull(invitation.workspaceId, currentUserId)
        if (workspaceMember?.role != Role.OWNER) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid role")
        }

        workspaceInvitationRepository.delete(invitation)
    }

    suspend fun joinToWorkspace(workspaceId: Long, code: String) = transactionalOperator.executeAndAwait {
        val currentUser = userService.getCurrentUserInfo()
        val invitation = workspaceInvitationRepository.findByWorkspaceIdAndCode(workspaceId, code)
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid invitation code")
        invitation.checkIsExpired()
        workspaceInvitationRepository.delete(invitation)

        val workspaceMember = WorkspaceMember(
            workspaceId = workspaceId,
            role = Role.MEMBER,
            userId = currentUser.id,
            profileImage = currentUser.profileImage,
            name = currentUser.name
        )

        runCatching {
            workspaceMemberRepository.save(workspaceMember)
        }.onFailure {
            if (it is DuplicateKeyException) {
                throw ResponseStatusException(HttpStatus.CONFLICT, "Already joined user")
            }
        }
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
            createdAt = createdAt,
            profileImage = profileImage
        )

    suspend fun getAllWorkspaceMember(workspaceId: Long): List<WorkspaceMemberDto> {
        val currentUserId = authenticationService.currentUserIdOrThrow()
        checkIsWorkspaceMember(workspaceId, currentUserId)
        return workspaceMemberRepository.findAllMemberByWorkspaceId(workspaceId)
    }

    private suspend fun checkIsWorkspaceMember(workspaceId: Long, userId: Long) {
        if (!isWorkspaceMember(workspaceId, userId)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "not member of workspace")
        }
    }

    suspend fun getWorkspaceMemberOrNull(workspaceId: Long, userId: Long): WorkspaceMember? {
        return workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
    }

    suspend fun isWorkspaceMember(workspaceId: Long, userId: Long): Boolean {
        return getWorkspaceMemberOrNull(workspaceId, userId) != null
    }
}

data class InvitationTarget(
    val email: String,
    val role: Role
)

data class WorkspaceListResponse(
    val workspaces: List<WorkspaceResponse>
)

data class WorkspaceResponse(
    val id: Long,
    val name: String,
    val createdAt: LocalDateTime,
    val profileImage: String?
)

data class WorkspaceMemberDto(
    val userId: Long,
    val memberId: Long,
    val name: String,
    val profileImage: String?,
    val role: String
)
