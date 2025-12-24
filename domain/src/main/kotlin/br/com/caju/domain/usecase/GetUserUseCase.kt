package br.com.caju.domain.usecase

import br.com.caju.domain.model.User
import br.com.caju.domain.port.driven.UserRepository
import java.util.UUID

class GetUserUseCase(private val userRepository: UserRepository) {
    fun execute(id: UUID): User? = userRepository.findById(id)

    fun executeAll(): List<User> = userRepository.findAll()
}
