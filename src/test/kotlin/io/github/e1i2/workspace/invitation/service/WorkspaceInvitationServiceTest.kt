package io.github.e1i2.workspace.invitation.service

import io.github.e1i2.user.adapter.MailSender
import io.github.e1i2.workspace.invitation.WorkspaceInvitation
import io.github.e1i2.workspace.invitation.repository.WorkspaceInvitationRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf

class WorkspaceInvitationServiceTest(
    private val workspaceInvitationRepository: WorkspaceInvitationRepository = mockk(),
    private val mailSender: MailSender = mockk(),
    private val workspaceInvitationService: WorkspaceInvitationService = WorkspaceInvitationService(
        workspaceInvitationRepository = workspaceInvitationRepository,
        mailSender = mailSender
    )
): StringSpec() {
    init {
        "Workspace 초대 저장 테스트" {
            val workspaceInvitation = WorkspaceInvitation(
                email = "test@gmail.com",
                workspaceId = 1
            )
            every { workspaceInvitationRepository.saveAll(any<List<WorkspaceInvitation>>()) } answers { flowOf(workspaceInvitation) }
            coEvery { mailSender.sendEmailAsync(any(), any(), workspaceInvitation.email) } coAnswers {  }

            workspaceInvitationService.send(workspaceInvitation.workspaceId, listOf(workspaceInvitation.email))

            verify(exactly = 1) { workspaceInvitationRepository.saveAll(any<List<WorkspaceInvitation>>()) }
            coVerify(exactly = 1) { mailSender.sendEmailAsync(any(), any(), workspaceInvitation.email) }
        }
    }
}