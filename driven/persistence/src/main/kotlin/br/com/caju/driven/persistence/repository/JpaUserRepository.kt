package br.com.caju.driven.persistence.repository

import br.com.caju.driven.persistence.entity.UserEntity
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaUserRepository : JpaRepository<UserEntity, UUID> {
    fun existsByEmail(email: String): Boolean
}
