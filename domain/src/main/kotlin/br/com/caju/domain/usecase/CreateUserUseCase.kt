package br.com.caju.domain.usecase

import br.com.caju.domain.model.User
import br.com.caju.domain.port.driven.EventPublisher
import br.com.caju.domain.port.driven.UserCreatedEvent
import br.com.caju.domain.port.driven.UserRepository
import br.com.caju.domain.port.driver.UserManagement

class CreateUserUseCase(
    private val userRepository: UserRepository,
    private val eventPublisher: EventPublisher
) {
    fun execute(user: User): User {
        if (userRepository.existsByEmail(user.email)) {
            throw IllegalArgumentException("User with email ${user.email} already exists")
        }

        val savedUser = userRepository.save(user)

        eventPublisher.publish(
            UserCreatedEvent(
                userId = savedUser.id.toString(),
                name = savedUser.name,
                email = savedUser.email
            )
        )

        return savedUser
    }
}
