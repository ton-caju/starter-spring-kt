package br.com.caju.driver.restserver.config

import br.com.caju.domain.port.driven.EventPublisher
import com.ninjasquad.springmockk.MockkBean
import io.mockk.justRun
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestTemplate
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@DirtiesContext
@Import(TestConfig::class)
abstract class RestAbstractIntegrationTest {

    @LocalServerPort protected var port: Int = 0

    @Autowired protected lateinit var restTemplate: RestTemplate

    @MockkBean protected lateinit var eventPublisher: EventPublisher

    protected fun baseUrl(path: String) = "http://localhost:$port$path"

    @BeforeEach
    fun setupMocks() {
        justRun { eventPublisher.publish(any()) }
    }

    companion object {
        @Container
        @ServiceConnection
        @JvmField
        val postgresContainer: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:16-alpine")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
    }
}
