package br.com.caju.driver.restserver.exception

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Standard error response following RFC 7807 Problem Details")
data class ErrorResponse(
    @Schema(description = "Timestamp when the error occurred", example = "2025-12-27T10:30:00")
    val timestamp: LocalDateTime = LocalDateTime.now(),
    @Schema(description = "HTTP status code", example = "400") val status: Int,
    @Schema(description = "HTTP status reason phrase", example = "Bad Request") val error: String,
    @Schema(
        description = "Error message describing what went wrong",
        example = "Validation failed for one or more fields",
    )
    val message: String,
    @Schema(description = "Request path that caused the error", example = "/api/users")
    val path: String,
    @Schema(
        description = "List of validation errors (only present for validation failures)",
        nullable = true,
    )
    val errors: List<ValidationError>? = null,
)

@Schema(description = "Validation error details for a specific field")
data class ValidationError(
    @Schema(description = "Field name that failed validation", example = "email") val field: String,
    @Schema(description = "Validation error message", example = "Invalid email format")
    val message: String,
    @Schema(description = "The value that was rejected", example = "invalid-email", nullable = true)
    val rejectedValue: Any? = null,
)
