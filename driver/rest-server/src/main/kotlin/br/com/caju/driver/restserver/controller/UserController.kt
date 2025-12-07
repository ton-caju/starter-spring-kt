package br.com.caju.driver.restserver.controller

import br.com.caju.domain.model.User
import br.com.caju.domain.usecase.CreateUserUseCase
import br.com.caju.domain.usecase.DeleteUserUseCase
import br.com.caju.domain.usecase.GetUserUseCase
import br.com.caju.domain.usecase.UpdateUserUseCase
import br.com.caju.driver.restserver.dto.UserRequest
import br.com.caju.driver.restserver.dto.UserResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) {

    @PostMapping
    fun createUser(@Valid @RequestBody request: UserRequest): ResponseEntity<UserResponse> {
        val user = User(
            name = request.name,
            email = request.email,
            phone = request.phone,
            birthday = request.birthday
        )
        val createdUser = createUserUseCase.execute(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromDomain(createdUser))
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<UserResponse> {
        val user = getUserUseCase.execute(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(UserResponse.fromDomain(user))
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserResponse>> {
        val users = getUserUseCase.executeAll()
        return ResponseEntity.ok(users.map { UserResponse.fromDomain(it) })
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UserRequest
    ): ResponseEntity<UserResponse> {
        val user = User(
            id = id,
            name = request.name,
            email = request.email,
            phone = request.phone,
            birthday = request.birthday
        )
        val updatedUser = updateUserUseCase.execute(user)
        return ResponseEntity.ok(UserResponse.fromDomain(updatedUser))
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<Void> {
        deleteUserUseCase.execute(id)
        return ResponseEntity.noContent().build()
    }
}
