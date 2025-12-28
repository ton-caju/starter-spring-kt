package br.com.caju.driver.restserver.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Past
import java.time.LocalDate

@Schema(description = "User creation/update request")
data class UserRequest(
    @field:NotBlank(message = "Name is required")
    @Schema(description = "User's full name", example = "John Doe", required = true)
    val name: String,
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    @Schema(
        description = "User's email address",
        example = "john.doe@example.com",
        required = true,
        format = "email",
    )
    val email: String,
    @field:NotBlank(message = "Phone is required")
    @Schema(
        description = "User's phone number with country code",
        example = "+5511999999999",
        required = true,
    )
    val phone: String,
    @field:Past(message = "Birthday must be in the past")
    @Schema(
        description = "User's date of birth (must be in the past)",
        example = "1990-01-15",
        required = true,
        format = "date",
    )
    val birthday: LocalDate,
)
