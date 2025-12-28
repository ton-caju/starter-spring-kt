package br.com.caju.domain.usecase

import br.com.caju.domain.exception.ResourceNotFoundException
import br.com.caju.domain.model.User
import br.com.caju.domain.port.driven.EventPublisher
import br.com.caju.domain.port.driven.UserRepository
import br.com.caju.domain.port.driven.UserUpdatedEvent
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import java.util.UUID

class UpdateUserUseCaseTest :
    BehaviorSpec({
        given("an UpdateUserUseCase") {
            val userRepository = mockk<UserRepository>()
            val eventPublisher = mockk<EventPublisher>(relaxed = true)
            val useCase = UpdateUserUseCase(userRepository, eventPublisher)

            `when`("updating an existing user") {
                val userId = UUID.randomUUID()
                val existingUser =
                    User(
                        id = userId,
                        name = "John Doe",
                        email = "john@example.com",
                        phone = "+5511999999999",
                        birthday = LocalDate.of(1990, 1, 1),
                    )

                val updatedUser = existingUser.copy(name = "John Updated", phone = "+5511777777777")

                every { userRepository.findById(userId) } returns existingUser
                every { userRepository.update(updatedUser) } returns updatedUser

                val result = useCase.execute(updatedUser)

                then("should update the user") { verify { userRepository.update(updatedUser) } }

                then("should publish user updated event") {
                    verify {
                        eventPublisher.publish(
                            UserUpdatedEvent(
                                userId = updatedUser.id.toString(),
                                name = updatedUser.name,
                                email = updatedUser.email,
                            )
                        )
                    }
                }

                then("should return the updated user") { result shouldBe updatedUser }
            }

            `when`("updating a non-existing user") {
                val userId = UUID.randomUUID()
                val user =
                    User(
                        id = userId,
                        name = "Ghost User",
                        email = "ghost@example.com",
                        phone = "+5511666666666",
                        birthday = LocalDate.of(1980, 10, 10),
                    )

                every { userRepository.findById(userId) } returns null

                then("should throw ResourceNotFoundException") {
                    val exception = shouldThrow<ResourceNotFoundException> { useCase.execute(user) }
                    exception.message shouldBe "User with id ${user.id} not found"
                }
            }
        }
    })
