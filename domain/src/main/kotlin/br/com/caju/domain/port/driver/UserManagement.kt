package br.com.caju.domain.port.driver

import br.com.caju.domain.model.User
import java.util.UUID

interface UserManagement {
    fun createUser(user: User): User
    fun getUserById(id: UUID): User?
    fun getAllUsers(): List<User>
    fun updateUser(user: User): User
    fun deleteUser(id: UUID)
}
