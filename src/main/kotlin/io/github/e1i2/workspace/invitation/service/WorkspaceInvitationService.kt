package io.github.e1i2.workspace.invitation.service

import io.github.e1i2.user.adapter.MailSender
import io.github.e1i2.workspace.invitation.WorkspaceInvitation
import io.github.e1i2.workspace.invitation.repository.WorkspaceInvitationRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class WorkspaceInvitationService(
    private val workspaceInvitationRepository: WorkspaceInvitationRepository,
    private val mailSender: MailSender
) {
    suspend fun send(workspaceId: Long, emails: List<String>) {
        val workspaceInvitations = buildWorkspaceInvitations(workspaceId, emails)
        val savedWorkspaceInvitations = workspaceInvitationRepository.saveAll(workspaceInvitations)
        savedWorkspaceInvitations.map { workspaceInvitation ->
            mailSender.sendEmailAsync("Workspace 초대 메일", workspaceInvitation.code, workspaceInvitation.email)
        }.collect()
    }

    private fun buildWorkspaceInvitations(workspaceId: Long, emails: List<String>): List<WorkspaceInvitation> {
        return emails.map { email ->
            WorkspaceInvitation(email = email, workspaceId = workspaceId)
        }
    }
}