package br.com.caju.driver.restserver.exception

import br.com.caju.driver.restserver.config.RestAbstractIntegrationTest
import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDate
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

class ValidationErrorIntegrationTest : RestAbstractIntegrationTest() {

    @Autowired private lateinit var webApplicationContext: WebApplicationContext

    @Autowired private lateinit var objectMapper: ObjectMapper

    private val mockMvc: MockMvc by lazy {
        MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    @Test
    fun `should return 400 with validation errors when birthday is in the future`() {
        // Given
        val invalidRequest =
            mapOf(
                "name" to "John Doe",
                "email" to "john@example.com",
                "phone" to "+5511999999999",
                "birthday" to LocalDate.now().plusDays(1).toString(), // Future date
            )

        // When & Then
        mockMvc
            .post("/api/users") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(invalidRequest)
            }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value(400) }
                jsonPath("$.error") { value("Bad Request") }
                jsonPath("$.message") { value("Validation failed for one or more fields") }
                jsonPath("$.path") { value("/api/users") }
                jsonPath("$.errors") { isArray() }
                jsonPath("$.errors[0].field") { value("birthday") }
                jsonPath("$.errors[0].message") { value("Birthday must be in the past") }
            }
    }

    @Test
    fun `should return 400 with multiple validation errors`() {
        // Given
        val invalidRequest =
            mapOf(
                "name" to "",
                "email" to "invalid-email",
                "phone" to "",
                "birthday" to LocalDate.now().plusDays(1).toString(),
            )

        // When & Then
        mockMvc
            .post("/api/users") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(invalidRequest)
            }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value(400) }
                jsonPath("$.errors.length()") { value(4) }
            }
    }

    // Note: 404 and UUID validation tests are better covered by UserControllerIntegrationTest
    // which uses RestTemplate and properly handles these scenarios
}
