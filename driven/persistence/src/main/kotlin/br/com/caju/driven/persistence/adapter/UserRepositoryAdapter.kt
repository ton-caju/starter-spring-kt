package br.com.caju.driven.persistence.adapter

import br.com.caju.domain.model.User
import br.com.caju.domain.port.driven.UserRepository
import br.com.caju.driven.persistence.mapper.UserMapper
import br.com.caju.driven.persistence.repository.JpaUserRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserRepositoryAdapter(
    private val jpaUserRepository: JpaUserRepository
) : UserRepository {

    override fun save(user: User): User {
        val entity = UserMapper.toEntity(user)
        val savedEntity = jpaUserRepository.save(entity)
        return UserMapper.toDomain(savedEntity)
    }

    override fun findById(id: UUID): User? {
        return jpaUserRepository.findById(id)
            .map { UserMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findAll(): List<User> {
        return jpaUserRepository.findAll()
            .map { UserMapper.toDomain(it) }
    }

    override fun update(user: User): User {
        if (!jpaUserRepository.existsById(user.id)) {
            throw IllegalArgumentException("User with id ${user.id} not found")
        }
        val entity = UserMapper.toEntity(user)
        val updatedEntity = jpaUserRepository.save(entity)
        return UserMapper.toDomain(updatedEntity)
    }

    override fun deleteById(id: UUID) {
        jpaUserRepository.deleteById(id)
    }

    override fun existsByEmail(email: String): Boolean {
        return jpaUserRepository.existsByEmail(email)
    }
}
