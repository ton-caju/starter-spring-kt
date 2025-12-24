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
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

@org.springframework.context.annotation.Import(
    br.com.caju.driver.restserver.config.TestConfig::class
)
@org.springframework.test.annotation.DirtiesContext
class UserControllerSimpleIntegrationTest : AbstractIntegrationTest() {

    @LocalServerPort private var port: Int = 0

    @Autowired private lateinit var restTemplate: RestTemplate

    @MockkBean private lateinit var eventPublisher: EventPublisher

    private fun baseUrl() = "http://localhost:$port/api/users"

    @Test
    fun `should create user successfully`() {
        justRun { eventPublisher.publish(any()) }

        val userRequest =
            UserRequest(
                name = "John Doe",
                email = "john.doe.create@example.com",
                phone = "+5511999999999",
                birthday = LocalDate.of(1990, 1, 1),
            )

        val response: ResponseEntity<UserResponse> =
            restTemplate.postForEntity(baseUrl(), userRequest)

        response.statusCode shouldBe HttpStatus.CREATED
        val user = response.body
        user.shouldNotBeNull()
        user.name shouldBe "John Doe"
        user.email shouldBe "john.doe.create@example.com"
    }
}
