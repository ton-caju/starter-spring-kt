package br.com.caju.driver.restserver.exception

import br.com.caju.domain.exception.BusinessValidationException
import br.com.caju.domain.exception.DuplicateResourceException
import br.com.caju.domain.exception.ResourceNotFoundException
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()
    private val request =
        mock(HttpServletRequest::class.java).apply { `when`(requestURI).thenReturn("/api/users") }

    @Test
    fun `should handle validation exception and return 400 with field errors`() {
        val bindingResult = BeanPropertyBindingResult(Any(), "userRequest")
        bindingResult.addError(
            FieldError(
                "userRequest",
                "birthday",
                "2025-12-28",
                false,
                arrayOf("Past.userRequest.birthday"),
                null,
                "Birthday must be in the past",
            )
        )
        bindingResult.addError(
            FieldError(
                "userRequest",
                "email",
                "",
                false,
                arrayOf("NotBlank.userRequest.email"),
                null,
                "Email is required",
            )
        )

        val methodParameter = mock(MethodParameter::class.java)
        val exception = MethodArgumentNotValidException(methodParameter, bindingResult)

        val response = handler.handleValidationException(exception, request)

        response.statusCode shouldBe HttpStatus.BAD_REQUEST
        val body = response.body
        body.shouldNotBeNull()
        body.status shouldBe 400
        body.error shouldBe "Bad Request"
        body.message shouldBe "Validation failed for one or more fields"
        body.path shouldBe "/api/users"
        body.errors.shouldNotBeNull()
        body.errors shouldHaveSize 2
        body.errors!![0].field shouldBe "birthday"
        body.errors[0].message shouldBe "Birthday must be in the past"
        body.errors[0].rejectedValue shouldBe "2025-12-28"
        body.errors[1].field shouldBe "email"
        body.errors[1].message shouldBe "Email is required"
    }

    @Test
    fun `should handle type mismatch exception and return 400`() {
        val exception =
            MethodArgumentTypeMismatchException(
                "invalid-uuid",
                java.util.UUID::class.java,
                "id",
                mock(MethodParameter::class.java),
                Throwable("Type mismatch"),
            )

        val response = handler.handleTypeMismatchException(exception, request)

        response.statusCode shouldBe HttpStatus.BAD_REQUEST
        val body = response.body
        body.shouldNotBeNull()
        body.status shouldBe 400
        body.error shouldBe "Bad Request"
        body.message shouldBe "Invalid value for parameter 'id': expected type UUID"
        body.path shouldBe "/api/users"
        body.errors shouldBe null
    }

    @Test
    fun `should handle resource not found exception and return 404`() {
        val exception = ResourceNotFoundException("User not found with id: 123")

        val response = handler.handleNotFoundException(exception, request)

        response.statusCode shouldBe HttpStatus.NOT_FOUND
        val body = response.body
        body.shouldNotBeNull()
        body.status shouldBe 404
        body.error shouldBe "Not Found"
        body.message shouldBe "User not found with id: 123"
        body.path shouldBe "/api/users"
        body.errors shouldBe null
    }

    @Test
    fun `should handle no such element exception and return 404`() {
        val exception = NoSuchElementException("User not found with id: 123")

        val response = handler.handleNotFoundException(exception, request)

        response.statusCode shouldBe HttpStatus.NOT_FOUND
        val body = response.body
        body.shouldNotBeNull()
        body.status shouldBe 404
        body.error shouldBe "Not Found"
        body.message shouldBe "User not found with id: 123"
        body.path shouldBe "/api/users"
        body.errors shouldBe null
    }

    @Test
    fun `should handle duplicate resource exception and return 409`() {
        val exception = DuplicateResourceException("User with email already exists")

        val response = handler.handleDuplicateResourceException(exception, request)

        response.statusCode shouldBe HttpStatus.CONFLICT
        val body = response.body
        body.shouldNotBeNull()
        body.status shouldBe 409
        body.error shouldBe "Conflict"
        body.message shouldBe "User with email already exists"
        body.path shouldBe "/api/users"
        body.errors shouldBe null
    }

    @Test
    fun `should handle business validation exception and return 422`() {
        val exception = BusinessValidationException("User age must be at least 18")

        val response = handler.handleBusinessValidationException(exception, request)

        response.statusCode shouldBe HttpStatus.UNPROCESSABLE_ENTITY
        val body = response.body
        body.shouldNotBeNull()
        body.status shouldBe 422
        body.error shouldBe "Unprocessable Entity"
        body.message shouldBe "User age must be at least 18"
        body.path shouldBe "/api/users"
        body.errors shouldBe null
    }

    @Test
    fun `should handle generic exception and return 500`() {
        val exception = RuntimeException("Unexpected database error")

        val response = handler.handleGenericException(exception, request)

        response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
        val body = response.body
        body.shouldNotBeNull()
        body.status shouldBe 500
        body.error shouldBe "Internal Server Error"
        body.message shouldBe "An unexpected error occurred. Please try again later."
        body.path shouldBe "/api/users"
        body.errors shouldBe null
    }
}
