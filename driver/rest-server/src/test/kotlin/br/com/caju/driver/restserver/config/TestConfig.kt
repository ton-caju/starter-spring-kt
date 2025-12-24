package br.com.caju.driver.restserver.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@TestConfiguration
class TestConfig {
    @Bean fun restTemplate(): RestTemplate = RestTemplate()
}
