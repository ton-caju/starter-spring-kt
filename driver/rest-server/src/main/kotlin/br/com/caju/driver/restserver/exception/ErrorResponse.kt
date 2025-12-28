package br.com.caju.driver.restserver.exception

import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val errors: List<ValidationError>? = null,
)

data class ValidationError(val field: String, val message: String, val rejectedValue: Any? = null)
