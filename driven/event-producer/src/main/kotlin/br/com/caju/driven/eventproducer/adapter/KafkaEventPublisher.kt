package br.com.caju.driven.eventproducer.adapter

import br.com.caju.domain.port.driven.DomainEvent
import br.com.caju.domain.port.driven.EventPublisher
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) : EventPublisher {

    override fun publish(event: DomainEvent) {
        val topic = "user-events"
        val eventJson = objectMapper.writeValueAsString(event)

        kafkaTemplate.send(topic, event.eventType, eventJson)
            .whenComplete { result, ex ->
                if (ex != null) {
                    println("Failed to publish event: ${event.eventType}, error: ${ex.message}")
                } else {
                    println("Event published successfully: ${event.eventType} to topic: $topic")
                }
            }
    }
}
