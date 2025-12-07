package br.com.caju.driven.persistence.mapper

import br.com.caju.domain.model.User
import br.com.caju.driven.persistence.entity.UserEntity

object UserMapper {
    fun toEntity(user: User): UserEntity {
        return UserEntity(
            id = user.id,
            name = user.name,
            email = user.email,
            phone = user.phone,
            birthday = user.birthday
        )
    }

    fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            phone = entity.phone,
            birthday = entity.birthday
        )
    }
}
