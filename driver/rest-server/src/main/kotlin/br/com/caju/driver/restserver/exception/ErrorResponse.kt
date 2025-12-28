package br.com.caju.driver.restserver.exception

import java.time.LocalDateTime

/**
 * Standard error response following RFC 7807 Problem Details format
 *
 * @property timestamp When the error occurred
 * @property status HTTP status code
 * @property error HTTP status text
 * @property message Error message
 * @property path Request path where the error occurred
 * @property errors Optional list of validation errors
 */
data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val errors: List<ValidationError>? = null,
)

/**
 * Represents a single validation error
 *
 * @property field Field name that failed validation
 * @property message Error message describing the validation failure
 * @property rejectedValue Value that was rejected (optional for security)
 */
data class ValidationError(val field: String, val message: String, val rejectedValue: Any? = null)
