package br.com.caju.domain.usecase

import br.com.caju.domain.model.User
import br.com.caju.domain.port.driven.EventPublisher
import br.com.caju.domain.port.driven.UserCreatedEvent
import br.com.caju.domain.port.driven.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import java.util.UUID

class CreateUserUseCaseTest :
    BehaviorSpec({
        given("a CreateUserUseCase") {
            val userRepository = mockk<UserRepository>()
            val eventPublisher = mockk<EventPublisher>(relaxed = true)
            val useCase = CreateUserUseCase(userRepository, eventPublisher)

            `when`("creating a new user with valid data") {
                val user =
                    User(
                        name = "John Doe",
                        email = "john@example.com",
                        phone = "+5511999999999",
                        birthday = LocalDate.of(1990, 1, 1),
                    )

                val savedUser = user.copy(id = UUID.randomUUID())

                every { userRepository.existsByEmail(user.email) } returns false
                every { userRepository.save(user) } returns savedUser

                val result = useCase.execute(user)

                then("should save the user") { verify { userRepository.save(user) } }

                then("should publish user created event") {
                    verify {
                        eventPublisher.publish(
                            UserCreatedEvent(
                                userId = savedUser.id.toString(),
                                name = savedUser.name,
                                email = savedUser.email,
                            )
                        )
                    }
                }

                then("should return the saved user") { result shouldBe savedUser }
            }

            `when`("creating a user with an existing email") {
                val user =
                    User(
                        name = "Jane Doe",
                        email = "existing@example.com",
                        phone = "+5511888888888",
                        birthday = LocalDate.of(1985, 5, 15),
                    )

                every { userRepository.existsByEmail(user.email) } returns true

                then("should throw IllegalArgumentException") {
                    val exception = shouldThrow<IllegalArgumentException> { useCase.execute(user) }
                    exception.message shouldBe "User with email ${user.email} already exists"
                }
            }
        }
    })
