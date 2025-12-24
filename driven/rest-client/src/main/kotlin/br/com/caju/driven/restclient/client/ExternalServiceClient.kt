package br.com.caju.driven.restclient.client

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Component
class ExternalServiceClient(private val webClient: WebClient) {
    fun validateEmail(email: String): Mono<Boolean> =
        webClient
            .get()
            .uri("/validate/email?email=$email")
            .retrieve()
            .bodyToMono<Boolean>()
            .onErrorReturn(false)

    fun validatePhone(phone: String): Mono<Boolean> =
        webClient
            .get()
            .uri("/validate/phone?phone=$phone")
            .retrieve()
            .bodyToMono<Boolean>()
            .onErrorReturn(false)
}
