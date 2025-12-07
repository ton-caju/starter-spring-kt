package br.com.caju.driven.persistence.config

import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["br.com.caju.driven.persistence.repository"])
@ComponentScan(basePackages = ["br.com.caju.driven.persistence"])
@EntityScan(basePackages = ["br.com.caju.driven.persistence"])
class PersistenceConfig
