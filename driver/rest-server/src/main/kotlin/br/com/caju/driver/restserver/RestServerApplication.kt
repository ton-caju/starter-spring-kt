package br.com.caju.driver.restserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["br.com.caju"])
class RestServerApplication

fun main(args: Array<String>) {
    runApplication<RestServerApplication>(*args)
}
