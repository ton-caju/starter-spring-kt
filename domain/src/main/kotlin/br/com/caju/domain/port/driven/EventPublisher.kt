package br.com.caju.domain.port.driven

interface EventPublisher {
    fun publish(event: DomainEvent)
}

sealed interface DomainEvent {
    val eventType: String
}

data class UserCreatedEvent(
    val userId: String,
    val name: String,
    val email: String
) : DomainEvent {
    override val eventType: String = "USER_CREATED"
}

data class UserUpdatedEvent(
    val userId: String,
    val name: String,
    val email: String
) : DomainEvent {
    override val eventType: String = "USER_UPDATED"
}

data class UserDeletedEvent(
    val userId: String
) : DomainEvent {
    override val eventType: String = "USER_DELETED"
}
