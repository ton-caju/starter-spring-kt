package br.com.caju.driver.restserver.controller

import br.com.caju.domain.port.driven.EventPublisher
import br.com.caju.driver.restserver.config.AbstractIntegrationTest
import br.com.caju.driver.restserver.dto.UserRequest
import br.com.caju.driver.restserver.dto.UserResponse
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.justRun
import java.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.client.getForEntity
import org.springframework.web.client.postForEntity

@Import(br.com.caju.driver.restserver.config.TestConfig::class)
@org.springframework.test.annotation.DirtiesContext
class UserControllerIntegrationTest : AbstractIntegrationTest() {

    @LocalServerPort private var port: Int = 0

    @Autowired private lateinit var restTemplate: RestTemplate

    @MockkBean private lateinit var eventPublisher: EventPublisher

    private fun baseUrl() = "http://localhost:$port/api/users"

    @BeforeEach
    fun setup() {
        justRun { eventPublisher.publish(any()) }
    }

    @Test
    fun `should create user successfully`() {
        val userRequest =
            UserRequest(
                name = "John Doe",
                email = "john.create.test@example.com",
                phone = "+5511999999999",
                birthday = LocalDate.of(1990, 1, 1),
            )

        val response: ResponseEntity<UserResponse> =
            restTemplate.postForEntity(baseUrl(), userRequest)

        response.statusCode shouldBe HttpStatus.CREATED
        val user = response.body
        user.shouldNotBeNull()
        user.id.shouldNotBeNull()
        user.name shouldBe "John Doe"
        user.email shouldBe "john.create.test@example.com"
        user.phone shouldBe "+5511999999999"
        user.birthday shouldBe LocalDate.of(1990, 1, 1)
    }

    @Test
    fun `should get user by id`() {
        // Create user first
        val userRequest =
            UserRequest(
                name = "Get Test User",
                email = "get.test.user@example.com",
                phone = "+5511777777777",
                birthday = LocalDate.of(1992, 3, 20),
            )

        val createResponse: ResponseEntity<UserResponse> =
            restTemplate.postForEntity(baseUrl(), userRequest)
        val createdUser = createResponse.body!!

        // Get the user
        val response: ResponseEntity<UserResponse> =
            restTemplate.getForEntity("${baseUrl()}/${createdUser.id}")

        response.statusCode shouldBe HttpStatus.OK
        val user = response.body
        user.shouldNotBeNull()
        user.id shouldBe createdUser.id
        user.name shouldBe "Get Test User"
        user.email shouldBe "get.test.user@example.com"
    }

    @Test
    fun `should get all users`() {
        // Create users
        val user1 =
            UserRequest(
                "User 1",
                "user1.list@example.com",
                "+5511111111111",
                LocalDate.of(1990, 1, 1),
            )
        val user2 =
            UserRequest(
                "User 2",
                "user2.list@example.com",
                "+5511222222222",
                LocalDate.of(1991, 2, 2),
            )

        restTemplate.postForEntity(baseUrl(), user1, UserResponse::class.java)
        restTemplate.postForEntity(baseUrl(), user2, UserResponse::class.java)

        val response: ResponseEntity<Array<UserResponse>> = restTemplate.getForEntity(baseUrl())

        response.statusCode shouldBe HttpStatus.OK
        val users = response.body
        users.shouldNotBeNull()
        (users.size >= 2) shouldBe true
    }

    @Test
    fun `should update user`() {
        // Create user first
        val userRequest =
            UserRequest(
                name = "Update Test",
                email = "update.test@example.com",
                phone = "+5511666666666",
                birthday = LocalDate.of(1988, 8, 8),
            )

        val createResponse: ResponseEntity<UserResponse> =
            restTemplate.postForEntity(baseUrl(), userRequest)
        val createdUser = createResponse.body!!

        // Update the user
        val updateRequest = userRequest.copy(name = "Updated Name", phone = "+5511555555555")

        val response: ResponseEntity<UserResponse> =
            restTemplate.exchange(
                "${baseUrl()}/${createdUser.id}",
                HttpMethod.PUT,
                HttpEntity(updateRequest),
            )

        response.statusCode shouldBe HttpStatus.OK
        val user = response.body
        user.shouldNotBeNull()
        user.id shouldBe createdUser.id
        user.name shouldBe "Updated Name"
        user.phone shouldBe "+5511555555555"
    }

    @Test
    fun `should delete user`() {
        // Create user first
        val userRequest =
            UserRequest(
                name = "Delete Test",
                email = "delete.test@example.com",
                phone = "+5511444444444",
                birthday = LocalDate.of(1987, 7, 7),
            )

        val createResponse: ResponseEntity<UserResponse> =
            restTemplate.postForEntity(baseUrl(), userRequest)
        val createdUser = createResponse.body!!

        // Delete the user
        restTemplate.delete("${baseUrl()}/${createdUser.id}")

        // Verify user is deleted
        try {
            restTemplate.getForEntity<UserResponse>("${baseUrl()}/${createdUser.id}")
            throw AssertionError("Expected HttpClientErrorException.NotFound")
        } catch (e: HttpClientErrorException.NotFound) {
            e.statusCode shouldBe HttpStatus.NOT_FOUND
        }
    }
}
