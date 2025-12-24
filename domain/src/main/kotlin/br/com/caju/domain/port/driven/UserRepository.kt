package br.com.caju.domain.port.driven

import br.com.caju.domain.model.User
import java.util.UUID

interface UserRepository {
    fun save(user: User): User

    fun findById(id: UUID): User?

    fun findAll(): List<User>

    fun update(user: User): User

    fun deleteById(id: UUID)

    fun existsByEmail(email: String): Boolean
}
