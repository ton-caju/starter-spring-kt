package br.com.caju.domain.usecase

import br.com.caju.domain.exception.ResourceNotFoundException
import br.com.caju.domain.model.User
import br.com.caju.domain.port.driven.EventPublisher
import br.com.caju.domain.port.driven.UserRepository
import br.com.caju.domain.port.driven.UserUpdatedEvent

class UpdateUserUseCase(
    private val userRepository: UserRepository,
    private val eventPublisher: EventPublisher,
) {
    fun execute(user: User): User {
        val existingUser =
            userRepository.findById(user.id)
                ?: throw ResourceNotFoundException("User with id ${user.id} not found")

        val updatedUser = userRepository.update(user)

        eventPublisher.publish(
            UserUpdatedEvent(
                userId = updatedUser.id.toString(),
                name = updatedUser.name,
                email = updatedUser.email,
            )
        )

        return updatedUser
    }
}
