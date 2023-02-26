package io.github.e1i2.workspace.controller

import io.github.e1i2.workspace.service.WorkspaceService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
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
    suspend fun saveWorkspace(@RequestBody @Valid request: CreateWorkspaceRequest) {
        workspaceService.saveWorkspace(request.workspaceName)
    }
}

data class CreateWorkspaceRequest(
    @NotBlank
    val workspaceName: String
)
