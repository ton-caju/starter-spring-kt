package br.com.caju.driver.restserver.controller

import br.com.caju.driver.restserver.config.RestAbstractIntegrationTest
import br.com.caju.driver.restserver.dto.UserRequest
import br.com.caju.driver.restserver.dto.UserResponse
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.postForEntity

class UserControllerSimpleIntegrationTest : RestAbstractIntegrationTest() {

    @Test
    fun `should create user successfully`() {
        val userRequest =
            UserRequest(
                name = "John Doe",
                email = "john.doe.create@example.com",
                phone = "+5511999999999",
                birthday = LocalDate.of(1990, 1, 1),
            )

        val response: ResponseEntity<UserResponse> =
            restTemplate.postForEntity(baseUrl("/api/users"), userRequest)

        response.statusCode shouldBe HttpStatus.CREATED
        val user = response.body
        user.shouldNotBeNull()
        user.name shouldBe "John Doe"
        user.email shouldBe "john.doe.create@example.com"
    }
}
