package br.com.caju.domain.usecase

import br.com.caju.domain.port.driven.EventPublisher
import br.com.caju.domain.port.driven.UserDeletedEvent
import br.com.caju.domain.port.driven.UserRepository
import java.util.UUID

class DeleteUserUseCase(
    private val userRepository: UserRepository,
    private val eventPublisher: EventPublisher
) {
    fun execute(id: UUID) {
        val user = userRepository.findById(id)
            ?: throw IllegalArgumentException("User with id $id not found")

        userRepository.deleteById(id)

        eventPublisher.publish(
            UserDeletedEvent(userId = id.toString())
        )
    }
}
