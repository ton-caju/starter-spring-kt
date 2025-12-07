package br.com.caju.driver.restserver.config

import br.com.caju.domain.port.driven.EventPublisher
import br.com.caju.domain.port.driven.UserRepository
import br.com.caju.domain.usecase.CreateUserUseCase
import br.com.caju.domain.usecase.DeleteUserUseCase
import br.com.caju.domain.usecase.GetUserUseCase
import br.com.caju.domain.usecase.UpdateUserUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCaseConfig {

    @Bean
    fun createUserUseCase(
        userRepository: UserRepository,
        eventPublisher: EventPublisher
    ): CreateUserUseCase {
        return CreateUserUseCase(userRepository, eventPublisher)
    }

    @Bean
    fun getUserUseCase(userRepository: UserRepository): GetUserUseCase {
        return GetUserUseCase(userRepository)
    }

    @Bean
    fun updateUserUseCase(
        userRepository: UserRepository,
        eventPublisher: EventPublisher
    ): UpdateUserUseCase {
        return UpdateUserUseCase(userRepository, eventPublisher)
    }

    @Bean
    fun deleteUserUseCase(
        userRepository: UserRepository,
        eventPublisher: EventPublisher
    ): DeleteUserUseCase {
        return DeleteUserUseCase(userRepository, eventPublisher)
    }
}
