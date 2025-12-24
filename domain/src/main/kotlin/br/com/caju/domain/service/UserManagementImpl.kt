package br.com.caju.domain.service

import br.com.caju.domain.model.User
import br.com.caju.domain.port.driver.UserManagement
import br.com.caju.domain.usecase.CreateUserUseCase
import br.com.caju.domain.usecase.DeleteUserUseCase
import br.com.caju.domain.usecase.GetUserUseCase
import br.com.caju.domain.usecase.UpdateUserUseCase
import java.util.UUID

class UserManagementImpl(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
) : UserManagement {

    override fun createUser(user: User): User = createUserUseCase.execute(user)

    override fun getUserById(id: UUID): User? = getUserUseCase.execute(id)

    override fun getAllUsers(): List<User> = getUserUseCase.executeAll()

    override fun updateUser(user: User): User = updateUserUseCase.execute(user)

    override fun deleteUser(id: UUID) {
        deleteUserUseCase.execute(id)
    }
}
