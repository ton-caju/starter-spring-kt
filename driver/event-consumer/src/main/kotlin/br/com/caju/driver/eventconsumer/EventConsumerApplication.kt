package br.com.caju.driver.eventconsumer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["br.com.caju"])
class EventConsumerApplication

fun main(args: Array<String>) {
    runApplication<EventConsumerApplication>(*args)
}
