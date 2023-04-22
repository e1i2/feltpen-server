package io.github.e1i2.workspace.controller

import com.ninjasquad.springmockk.MockkBean
import io.github.e1i2.workspace.service.WorkspaceService
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(WorkspaceController::class)
class WorkspaceControllerTest(
    private val webTestClient: WebTestClient,
    @MockkBean
    private val workspaceService: WorkspaceService = mockk()
): StringSpec() {
    init {
        "Workspace 저장 테스트" {
            val workspaceName = "test"
            coEvery { workspaceService.saveWorkspace(workspaceName) } coAnswers { 1L }
            val createWorkspaceRequest = buildCreateWorkspaceRequest(workspaceName)

            val result = sendRequest(createWorkspaceRequest)

            result.expectStatus().isCreated
            coVerify(exactly = 1) { workspaceService.saveWorkspace(workspaceName) }
        }
    }

    private fun buildCreateWorkspaceRequest(name: String = "default") =
        CreateWorkspaceRequest(name)

    private fun sendRequest(request: CreateWorkspaceRequest): WebTestClient.ResponseSpec {
        return webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .mutateWith(SecurityMockServerConfigurers.mockUser())
            .post()
            .uri("/workspaces")
            .bodyValue(request)
            .exchange()
    }

}