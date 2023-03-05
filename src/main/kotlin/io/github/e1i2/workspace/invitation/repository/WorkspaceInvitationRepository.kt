package io.github.e1i2.workspace.invitation.repository

import io.github.e1i2.workspace.invitation.WorkspaceInvitation
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface WorkspaceInvitationRepository: CoroutineCrudRepository<WorkspaceInvitation, Long>
