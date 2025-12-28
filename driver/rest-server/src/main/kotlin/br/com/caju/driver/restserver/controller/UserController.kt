package br.com.caju.driver.restserver.controller

import br.com.caju.domain.model.User
import br.com.caju.domain.port.driver.UserManagement
import br.com.caju.driver.restserver.documentation.ApiBusinessValidationErrorResponse
import br.com.caju.driver.restserver.documentation.ApiConflictErrorResponse
import br.com.caju.driver.restserver.documentation.ApiInternalServerErrorResponse
import br.com.caju.driver.restserver.documentation.ApiInvalidUuidErrorResponse
import br.com.caju.driver.restserver.documentation.ApiNotFoundErrorResponse
import br.com.caju.driver.restserver.documentation.ApiValidationErrorResponse
import br.com.caju.driver.restserver.dto.UserRequest
import br.com.caju.driver.restserver.dto.UserResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "User Management", description = "APIs for managing users")
@RestController
@RequestMapping("/api/users")
class UserController(private val userManagement: UserManagement) {

    @Operation(
        summary = "Create a new user",
        description = "Creates a new user with the provided information",
    )
    @ApiResponse(
        responseCode = "201",
        description = "User successfully created",
        content = [Content(schema = Schema(implementation = UserResponse::class))],
    )
    @ApiValidationErrorResponse
    @ApiConflictErrorResponse
    @ApiInternalServerErrorResponse
    @PostMapping
    fun createUser(@Valid @RequestBody request: UserRequest): ResponseEntity<UserResponse> {
        val user =
            User(
                name = request.name,
                email = request.email,
                phone = request.phone,
                birthday = request.birthday,
            )
        val createdUser = userManagement.createUser(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromDomain(createdUser))
    }

    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a user by their unique identifier",
    )
    @ApiResponse(
        responseCode = "200",
        description = "User found",
        content = [Content(schema = Schema(implementation = UserResponse::class))],
    )
    @ApiInvalidUuidErrorResponse
    @ApiNotFoundErrorResponse
    @ApiInternalServerErrorResponse
    @GetMapping("/{id}")
    fun getUserById(
        @Parameter(description = "User unique identifier", required = true) @PathVariable id: UUID
    ): ResponseEntity<UserResponse> {
        val user = userManagement.getUserById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(UserResponse.fromDomain(user))
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    @ApiResponse(
        responseCode = "200",
        description = "List of users retrieved successfully",
        content = [Content(schema = Schema(implementation = UserResponse::class))],
    )
    @ApiInternalServerErrorResponse
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserResponse>> {
        val users = userManagement.getAllUsers()
        return ResponseEntity.ok(users.map { UserResponse.fromDomain(it) })
    }

    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @ApiResponse(
        responseCode = "200",
        description = "User successfully updated",
        content = [Content(schema = Schema(implementation = UserResponse::class))],
    )
    @ApiValidationErrorResponse
    @ApiNotFoundErrorResponse
    @ApiBusinessValidationErrorResponse
    @ApiInternalServerErrorResponse
    @PutMapping("/{id}")
    fun updateUser(
        @Parameter(description = "User unique identifier", required = true) @PathVariable id: UUID,
        @Valid @RequestBody request: UserRequest,
    ): ResponseEntity<UserResponse> {
        val user =
            User(
                id = id,
                name = request.name,
                email = request.email,
                phone = request.phone,
                birthday = request.birthday,
            )
        val updatedUser = userManagement.updateUser(user)
        return ResponseEntity.ok(UserResponse.fromDomain(updatedUser))
    }

    @Operation(summary = "Delete user", description = "Deletes a user by their unique identifier")
    @ApiResponse(
        responseCode = "204",
        description = "User successfully deleted",
        content = [Content()],
    )
    @ApiInvalidUuidErrorResponse
    @ApiNotFoundErrorResponse
    @ApiInternalServerErrorResponse
    @DeleteMapping("/{id}")
    fun deleteUser(
        @Parameter(description = "User unique identifier", required = true) @PathVariable id: UUID
    ): ResponseEntity<Void> {
        userManagement.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}
