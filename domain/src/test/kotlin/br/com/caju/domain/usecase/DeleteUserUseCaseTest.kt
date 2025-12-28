package br.com.caju.domain.usecase

import br.com.caju.domain.exception.ResourceNotFoundException
import br.com.caju.domain.model.User
import br.com.caju.domain.port.driven.EventPublisher
import br.com.caju.domain.port.driven.UserDeletedEvent
import br.com.caju.domain.port.driven.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import java.util.UUID

class DeleteUserUseCaseTest :
    BehaviorSpec({
        given("a DeleteUserUseCase") {
            val userRepository = mockk<UserRepository>()
            val eventPublisher = mockk<EventPublisher>(relaxed = true)
            val useCase = DeleteUserUseCase(userRepository, eventPublisher)

            `when`("deleting an existing user") {
                val userId = UUID.randomUUID()
                val user =
                    User(
                        id = userId,
                        name = "John Doe",
                        email = "john@example.com",
                        phone = "+5511999999999",
                        birthday = LocalDate.of(1990, 1, 1),
                    )

                every { userRepository.findById(userId) } returns user
                every { userRepository.deleteById(userId) } returns Unit

                useCase.execute(userId)

                then("should delete the user") { verify { userRepository.deleteById(userId) } }

                then("should publish user deleted event") {
                    verify { eventPublisher.publish(UserDeletedEvent(userId = userId.toString())) }
                }
            }

            `when`("deleting a non-existing user") {
                val userId = UUID.randomUUID()

                every { userRepository.findById(userId) } returns null

                then("should throw ResourceNotFoundException") {
                    val exception =
                        shouldThrow<ResourceNotFoundException> { useCase.execute(userId) }
                    exception.message shouldBe "User with id $userId not found"
                }
            }
        }
    })
