package br.com.caju.domain.usecase

import br.com.caju.domain.model.User
import br.com.caju.domain.port.driven.UserRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import java.util.UUID

class GetUserUseCaseTest :
    BehaviorSpec({
        given("a GetUserUseCase") {
            val userRepository = mockk<UserRepository>()
            val useCase = GetUserUseCase(userRepository)

            `when`("getting a user by id that exists") {
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

                val result = useCase.execute(userId)

                then("should return the user") { result shouldBe user }
            }

            `when`("getting a user by id that does not exist") {
                val userId = UUID.randomUUID()

                every { userRepository.findById(userId) } returns null

                val result = useCase.execute(userId)

                then("should return null") { result.shouldBeNull() }
            }

            `when`("getting all users") {
                val users =
                    listOf(
                        User(
                            id = UUID.randomUUID(),
                            name = "John Doe",
                            email = "john@example.com",
                            phone = "+5511999999999",
                            birthday = LocalDate.of(1990, 1, 1),
                        ),
                        User(
                            id = UUID.randomUUID(),
                            name = "Jane Smith",
                            email = "jane@example.com",
                            phone = "+5511888888888",
                            birthday = LocalDate.of(1985, 5, 15),
                        ),
                    )

                every { userRepository.findAll() } returns users

                val result = useCase.executeAll()

                then("should return all users") { result shouldContainExactly users }
            }

            `when`("getting all users when repository is empty") {
                every { userRepository.findAll() } returns emptyList()

                val result = useCase.executeAll()

                then("should return empty list") { result shouldBe emptyList() }
            }
        }
    })
