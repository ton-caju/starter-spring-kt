package br.com.caju.driver.restserver.config

import br.com.caju.domain.port.driven.EventPublisher
import br.com.caju.domain.port.driven.UserRepository
import br.com.caju.domain.port.driver.UserManagement
import br.com.caju.domain.service.UserManagementImpl
import br.com.caju.domain.usecase.CreateUserUseCase
import br.com.caju.domain.usecase.DeleteUserUseCase
import br.com.caju.domain.usecase.GetUserUseCase
import br.com.caju.domain.usecase.UpdateUserUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainServiceConfig {

    @Bean
    fun userManagement(
        userRepository: UserRepository,
        eventPublisher: EventPublisher
    ): UserManagement {
        // Use cases são criados internamente e não expostos como beans públicos
        val createUserUseCase = CreateUserUseCase(userRepository, eventPublisher)
        val getUserUseCase = GetUserUseCase(userRepository)
        val updateUserUseCase = UpdateUserUseCase(userRepository, eventPublisher)
        val deleteUserUseCase = DeleteUserUseCase(userRepository, eventPublisher)

        return UserManagementImpl(
            createUserUseCase,
            getUserUseCase,
            updateUserUseCase,
            deleteUserUseCase
        )
    }
}
