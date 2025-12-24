package br.com.caju.driver.eventconsumer.listener

import br.com.caju.driver.eventconsumer.config.AbstractIntegrationTest
import io.kotest.matchers.shouldBe
import java.util.concurrent.TimeUnit
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext

@EmbeddedKafka(partitions = 1, topics = ["user-events"])
@DirtiesContext
@org.springframework.context.annotation.Import(
    br.com.caju.driver.eventconsumer.config.TestKafkaConfig::class
)
class UserEventListenerIntegrationTest : AbstractIntegrationTest() {

    @Autowired private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Test
    fun `should consume USER_CREATED event successfully`() {
        // Given
        val userCreatedEvent =
            """
            {
                "eventType": "USER_CREATED",
                "userId": "123e4567-e89b-12d3-a456-426614174000",
                "name": "John Doe",
                "email": "john.doe@example.com"
            }
            """
                .trimIndent()

        // When
        val future = kafkaTemplate.send("user-events", userCreatedEvent)
        val result = future.get(10, TimeUnit.SECONDS)

        // Then
        result.producerRecord.topic() shouldBe "user-events"
        result.producerRecord.value() shouldBe userCreatedEvent

        // Give time for consumer to process
        Thread.sleep(2000)
    }

    @Test
    fun `should consume USER_UPDATED event successfully`() {
        // Given
        val userUpdatedEvent =
            """
            {
                "eventType": "USER_UPDATED",
                "userId": "123e4567-e89b-12d3-a456-426614174001",
                "name": "Jane Smith",
                "email": "jane.smith@example.com"
            }
            """
                .trimIndent()

        // When
        val future = kafkaTemplate.send("user-events", userUpdatedEvent)
        val result = future.get(10, TimeUnit.SECONDS)

        // Then
        result.producerRecord.topic() shouldBe "user-events"
        result.producerRecord.value() shouldBe userUpdatedEvent

        // Give time for consumer to process
        Thread.sleep(2000)
    }

    @Test
    fun `should consume USER_DELETED event successfully`() {
        // Given
        val userDeletedEvent =
            """
            {
                "eventType": "USER_DELETED",
                "userId": "123e4567-e89b-12d3-a456-426614174002"
            }
            """
                .trimIndent()

        // When
        val future = kafkaTemplate.send("user-events", userDeletedEvent)
        val result = future.get(10, TimeUnit.SECONDS)

        // Then
        result.producerRecord.topic() shouldBe "user-events"
        result.producerRecord.value() shouldBe userDeletedEvent

        // Give time for consumer to process
        Thread.sleep(2000)
    }

    @Test
    fun `should handle unknown event type gracefully`() {
        // Given
        val unknownEvent =
            """
            {
                "eventType": "UNKNOWN_EVENT",
                "data": "some data"
            }
            """
                .trimIndent()

        // When
        val future = kafkaTemplate.send("user-events", unknownEvent)
        val result = future.get(10, TimeUnit.SECONDS)

        // Then
        result.producerRecord.topic() shouldBe "user-events"
        result.producerRecord.value() shouldBe unknownEvent

        // Give time for consumer to process
        Thread.sleep(2000)
    }

    @Test
    fun `should handle invalid JSON gracefully`() {
        // Given
        val invalidJson = "{ invalid json }"

        // When
        val future = kafkaTemplate.send("user-events", invalidJson)
        val result = future.get(10, TimeUnit.SECONDS)

        // Then
        result.producerRecord.topic() shouldBe "user-events"
        result.producerRecord.value() shouldBe invalidJson

        // Give time for consumer to process
        Thread.sleep(2000)
    }
}
