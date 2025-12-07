package br.com.caju.driven.persistence.repository

import br.com.caju.driven.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaUserRepository : JpaRepository<UserEntity, UUID> {
    fun existsByEmail(email: String): Boolean
}
