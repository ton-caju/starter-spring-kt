package br.com.caju.driver.restserver.dto

import br.com.caju.domain.model.User
import java.time.LocalDate
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val phone: String,
    val birthday: LocalDate
) {
    companion object {
        fun fromDomain(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                name = user.name,
                email = user.email,
                phone = user.phone,
                birthday = user.birthday
            )
        }
    }
}
