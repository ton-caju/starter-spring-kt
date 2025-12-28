package br.com.caju.driver.restserver.dto

import br.com.caju.domain.model.User
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.util.UUID

@Schema(description = "User response with all details")
data class UserResponse(
    @Schema(
        description = "User's unique identifier",
        example = "123e4567-e89b-12d3-a456-426614174000",
        format = "uuid",
    )
    val id: UUID,
    @Schema(description = "User's full name", example = "John Doe") val name: String,
    @Schema(
        description = "User's email address",
        example = "john.doe@example.com",
        format = "email",
    )
    val email: String,
    @Schema(description = "User's phone number with country code", example = "+5511999999999")
    val phone: String,
    @Schema(description = "User's date of birth", example = "1990-01-15", format = "date")
    val birthday: LocalDate,
) {
    companion object {
        fun fromDomain(user: User): UserResponse =
            UserResponse(
                id = user.id,
                name = user.name,
                email = user.email,
                phone = user.phone,
                birthday = user.birthday,
            )
    }
}
