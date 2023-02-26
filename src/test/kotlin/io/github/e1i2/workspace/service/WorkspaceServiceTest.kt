package io.github.e1i2.workspace.service

import io.github.e1i2.workspace.Workspace
import io.github.e1i2.workspace.repository.WorkspaceRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class WorkspaceServiceTest(
    private val workspaceRepository: WorkspaceRepository = mockk(),
    private val workspaceService: WorkspaceService = WorkspaceService(
        workspaceRepository = workspaceRepository
    )
): StringSpec() {
    init {
        "Workspace 저장 테스트" {
            val workspaceName = "test"
            coEvery { workspaceRepository.save(any()) } coAnswers { Workspace(id = 1, name = workspaceName) }

            workspaceService.saveWorkspace(workspaceName)

            coVerify(exactly = 1) { workspaceRepository.save(any()) }
        }
    }
}