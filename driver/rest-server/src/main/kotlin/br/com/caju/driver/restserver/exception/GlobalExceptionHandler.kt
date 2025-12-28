package br.com.caju.driver.restserver.exception

import br.com.caju.domain.exception.BusinessValidationException
import br.com.caju.domain.exception.DuplicateResourceException
import br.com.caju.domain.exception.ResourceNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Validation error on path ${request.requestURI}")

        val errors =
            ex.bindingResult.fieldErrors.map { error ->
                ValidationError(
                    field = error.field,
                    message = error.defaultMessage ?: "Invalid value",
                    rejectedValue = error.rejectedValue,
                )
            }

        val errorResponse =
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = HttpStatus.BAD_REQUEST.reasonPhrase,
                message = "Validation failed for one or more fields",
                path = request.requestURI,
                errors = errors,
            )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchException(
        ex: MethodArgumentTypeMismatchException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Type mismatch error on path ${request.requestURI}: ${ex.message}")

        val errorResponse =
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = HttpStatus.BAD_REQUEST.reasonPhrase,
                message =
                    "Invalid value for parameter '${ex.name}': expected type ${ex.requiredType?.simpleName}",
                path = request.requestURI,
            )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(ResourceNotFoundException::class, NoSuchElementException::class)
    fun handleNotFoundException(
        ex: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Resource not found on path ${request.requestURI}: ${ex.message}")

        val errorResponse =
            ErrorResponse(
                status = HttpStatus.NOT_FOUND.value(),
                error = HttpStatus.NOT_FOUND.reasonPhrase,
                message = ex.message ?: "Resource not found",
                path = request.requestURI,
            )

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(DuplicateResourceException::class)
    fun handleDuplicateResourceException(
        ex: DuplicateResourceException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Duplicate resource on path ${request.requestURI}: ${ex.message}")

        val errorResponse =
            ErrorResponse(
                status = HttpStatus.CONFLICT.value(),
                error = HttpStatus.CONFLICT.reasonPhrase,
                message = ex.message ?: "Resource already exists",
                path = request.requestURI,
            )

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(BusinessValidationException::class)
    fun handleBusinessValidationException(
        ex: BusinessValidationException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Business validation error on path ${request.requestURI}: ${ex.message}")

        val errorResponse =
            ErrorResponse(
                status = HttpStatus.UNPROCESSABLE_ENTITY.value(),
                error = HttpStatus.UNPROCESSABLE_ENTITY.reasonPhrase,
                message = ex.message ?: "Business validation failed",
                path = request.requestURI,
            )

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error on path ${request.requestURI}", ex)

        val errorResponse =
            ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
                message = "An unexpected error occurred. Please try again later.",
                path = request.requestURI,
            )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}
