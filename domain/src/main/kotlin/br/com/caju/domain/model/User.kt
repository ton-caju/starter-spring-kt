package br.com.caju.domain.model

import java.time.LocalDate
import java.util.UUID

data class User(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val email: String,
    val phone: String,
    val birthday: LocalDate,
) {
    init {
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(email.isNotBlank() && email.contains("@")) { "Invalid email format" }
        require(phone.isNotBlank()) { "Phone cannot be blank" }
    }
}
