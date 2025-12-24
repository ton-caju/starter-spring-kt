package br.com.caju.driven.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "users")
class UserEntity(
    @Id @Column(columnDefinition = "UUID") val id: UUID = UUID.randomUUID(),
    @Column(nullable = false) val name: String,
    @Column(nullable = false, unique = true) val email: String,
    @Column(nullable = false) val phone: String,
    @Column(nullable = false) val birthday: LocalDate,
)
