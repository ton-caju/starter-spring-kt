package br.com.caju.driver.restserver.controller

import br.com.caju.domain.model.User
import br.com.caju.domain.port.driver.UserManagement
import br.com.caju.driver.restserver.dto.UserRequest
import br.com.caju.driver.restserver.dto.UserResponse
import br.com.caju.driver.restserver.exception.ErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "201",
                    description = "User successfully created",
                    content = [Content(schema = Schema(implementation = UserResponse::class))],
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "Validation failed - Invalid input data",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Validation Error",
                                            value =
                                                """
                                            {
                                              "timestamp": "2025-12-27T10:30:00",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Validation failed for one or more fields",
                                              "path": "/api/users",
                                              "errors": [
                                                {
                                                  "field": "email",
                                                  "message": "Invalid email format",
                                                  "rejectedValue": "invalid-email"
                                                },
                                                {
                                                  "field": "birthday",
                                                  "message": "Birthday must be in the past",
                                                  "rejectedValue": "2025-12-28"
                                                }
                                              ]
                                            }
                                            """,
                                        )
                                    ],
                            )
                        ],
                ),
                ApiResponse(
                    responseCode = "409",
                    description = "Conflict - User with email already exists",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Duplicate Email",
                                            value =
                                                """
                                            {
                                              "timestamp": "2025-12-27T10:30:00",
                                              "status": 409,
                                              "error": "Conflict",
                                              "message": "User with email john.doe@example.com already exists",
                                              "path": "/api/users"
                                            }
                                            """,
                                        )
                                    ],
                            )
                        ],
                ),
                ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Server Error",
                                            value =
                                                """
                                            {
                                              "timestamp": "2025-12-27T10:30:00",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred. Please try again later.",
                                              "path": "/api/users"
                                            }
                                            """,
                                        )
                                    ],
                            )
                        ],
                ),
            ]
    )
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
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = [Content(schema = Schema(implementation = UserResponse::class))],
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "Invalid UUID format",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Invalid UUID",
                                            value =
                                                """
                                            {
                                              "timestamp": "2025-12-27T10:30:00",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Invalid value for parameter 'id': expected type UUID",
                                              "path": "/api/users/invalid-uuid"
                                            }
                                            """,
                                        )
                                    ],
                            )
                        ],
                ),
                ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Not Found",
                                            value =
                                                """
                                            {
                                              "timestamp": "2025-12-27T10:30:00",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Resource not found",
                                              "path": "/api/users/123e4567-e89b-12d3-a456-426614174000"
                                            }
                                            """,
                                        )
                                    ],
                            )
                        ],
                ),
                ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Server Error",
                                            value =
                                                """
                                            {
                                              "timestamp": "2025-12-27T10:30:00",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred. Please try again later.",
                                              "path": "/api/users/123e4567-e89b-12d3-a456-426614174000"
                                            }
                                            """,
                                        )
                                    ],
                            )
                        ],
                ),
            ]
    )
    @GetMapping("/{id}")
    fun getUserById(
        @Parameter(description = "User unique identifier", required = true) @PathVariable id: UUID
    ): ResponseEntity<UserResponse> {
        val user = userManagement.getUserById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(UserResponse.fromDomain(user))
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "List of users retrieved successfully",
                    content = [Content(schema = Schema(implementation = UserResponse::class))],
                ),
                ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Server Error",
                                            value =
                                                """
                                            {
                                              "timestamp": "2025-12-27T10:30:00",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred. Please try again later.",
                                              "path": "/api/users"
                                            }
                                            """,
                                        )
                                    ],
                            )
                        ],
                ),
            ]
    )
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserResponse>> {
        val users = userManagement.getAllUsers()
        return ResponseEntity.ok(users.map { UserResponse.fromDomain(it) })
    }

    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "User successfully updated",
                    content = [Content(schema = Schema(implementation = UserResponse::class))],
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "Validation failed - Invalid input data or invalid UUID format",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Validation Error",
                                            value =
                                                """
                                            {
                                              "timestamp": "2025-12-27T10:30:00",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Validation failed for one or more fields",
                                              "path": "/api/users/123e4567-e89b-12d3-a456-426614174000",
                                              "errors": [
                                                {
                                                  "field": "email",
                                                  "message": "Invalid email format",
                                                  "rejectedValue": "invalid-email"
                                                }
                                              ]
                                            }
                                            """,
                                        )
                                    ],
                            )
                        ],
                ),
                ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Not Found",
                                            value =
                                                """
                                            {
                                              "timestamp": "2025-12-27T10:30:00",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "User with id 123e4567-e89b-12d3-a456-426614174000 not found",
                                              "path": "/api/users/123e4567-e89b-12d3-a456-426614174000"
                                            }
                                            """,
                                        )
                                    ],
                            )
                        ],
                ),
                ApiResponse(
                    responseCode = "422",
                    description = "Business validation failed",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Business Rule Violation",
                                            value =
                                                """
                                            {
                                              "timestamp": "2025-12-27T10:30:00",
                                              "status": 422,
                                              "error": "Unprocessable Entity",
                                              "message": "User age must be at least 18",
                                              "path": "/api/users/123e4567-e89b-12d3-a456-426614174000"
                                            }
                                            """,
                                        )
                                    ],
                            )
                        ],
                ),
                ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Server Error",
                                            value =
                                                """
                                            {
                                              "timestamp": "2025-12-27T10:30:00",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred. Please try again later.",
                                              "path": "/api/users/123e4567-e89b-12d3-a456-426614174000"
                                            }
                                            """,
                                        )
                                    ],
                            )
                        ],
                ),
            ]
    )
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
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "204",
                    description = "User successfully deleted",
                    content = [Content()],
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "Invalid UUID format",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Invalid UUID",
                                            value =
                                                """
                                            {
                                              "timestamp": "2025-12-27T10:30:00",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Invalid value for parameter 'id': expected type UUID",
                                              "path": "/api/users/invalid-uuid"
                                            }
                                            """,
                                        )
                                    ],
                            )
                        ],
                ),
                ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Not Found",
                                            value =
                                                """
                                            {
                                              "timestamp": "2025-12-27T10:30:00",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "User with id 123e4567-e89b-12d3-a456-426614174000 not found",
                                              "path": "/api/users/123e4567-e89b-12d3-a456-426614174000"
                                            }
                                            """,
                                        )
                                    ],
                            )
                        ],
                ),
                ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Server Error",
                                            value =
                                                """
                                            {
                                              "timestamp": "2025-12-27T10:30:00",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred. Please try again later.",
                                              "path": "/api/users/123e4567-e89b-12d3-a456-426614174000"
                                            }
                                            """,
                                        )
                                    ],
                            )
                        ],
                ),
            ]
    )
    @DeleteMapping("/{id}")
    fun deleteUser(
        @Parameter(description = "User unique identifier", required = true) @PathVariable id: UUID
    ): ResponseEntity<Void> {
        userManagement.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}
