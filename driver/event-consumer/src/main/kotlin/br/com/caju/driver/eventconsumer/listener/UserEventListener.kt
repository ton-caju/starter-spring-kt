package br.com.caju.driver.eventconsumer.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class UserEventListener(private val objectMapper: ObjectMapper) {

    @KafkaListener(topics = ["user-events"], groupId = "user-event-consumer-group")
    fun handleUserEvent(message: String) {
        try {
            println("Received event: $message")

            // Parse and process the event
            val event = objectMapper.readValue(message, Map::class.java)
            val eventType = event["eventType"] as? String

            when (eventType) {
                "USER_CREATED" -> handleUserCreated(event)
                "USER_UPDATED" -> handleUserUpdated(event)
                "USER_DELETED" -> handleUserDeleted(event)
                else -> println("Unknown event type: $eventType")
            }
        } catch (e: Exception) {
            println("Error processing event: ${e.message}")
        }
    }

    private fun handleUserCreated(event: Map<*, *>) {
        val userId = event["userId"]
        val name = event["name"]
        val email = event["email"]
        println("Processing USER_CREATED event - UserId: $userId, Name: $name, Email: $email")
        // Add your business logic here (e.g., send welcome email, update cache, etc.)
    }

    private fun handleUserUpdated(event: Map<*, *>) {
        val userId = event["userId"]
        val name = event["name"]
        val email = event["email"]
        println("Processing USER_UPDATED event - UserId: $userId, Name: $name, Email: $email")
        // Add your business logic here
    }

    private fun handleUserDeleted(event: Map<*, *>) {
        val userId = event["userId"]
        println("Processing USER_DELETED event - UserId: $userId")
        // Add your business logic here
    }
}
