package br.com.caju.driver.restserver.documentation

import br.com.caju.driver.restserver.exception.ErrorResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponse(
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
                              "path": "/api/resource",
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
)
annotation class ApiValidationErrorResponse

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponse(
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
                              "path": "/api/resource/invalid-uuid"
                            }
                            """,
                        )
                    ],
            )
        ],
)
annotation class ApiInvalidUuidErrorResponse

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponse(
    responseCode = "404",
    description = "Resource not found",
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
                              "path": "/api/resource/123e4567-e89b-12d3-a456-426614174000"
                            }
                            """,
                        )
                    ],
            )
        ],
)
annotation class ApiNotFoundErrorResponse

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponse(
    responseCode = "409",
    description = "Conflict - Resource already exists",
    content =
        [
            Content(
                schema = Schema(implementation = ErrorResponse::class),
                examples =
                    [
                        ExampleObject(
                            name = "Duplicate Resource",
                            value =
                                """
                            {
                              "timestamp": "2025-12-27T10:30:00",
                              "status": 409,
                              "error": "Conflict",
                              "message": "User with email john.doe@example.com already exists",
                              "path": "/api/resource"
                            }
                            """,
                        )
                    ],
            )
        ],
)
annotation class ApiConflictErrorResponse

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponse(
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
                              "path": "/api/resource"
                            }
                            """,
                        )
                    ],
            )
        ],
)
annotation class ApiBusinessValidationErrorResponse

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponse(
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
                              "path": "/api/resource"
                            }
                            """,
                        )
                    ],
            )
        ],
)
annotation class ApiInternalServerErrorResponse
