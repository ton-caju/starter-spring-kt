package br.com.caju.domain.exception

open class DomainException(message: String) : RuntimeException(message)

class BusinessValidationException(message: String) : DomainException(message)

class ResourceNotFoundException(message: String) : DomainException(message)

class DuplicateResourceException(message: String) : DomainException(message)
