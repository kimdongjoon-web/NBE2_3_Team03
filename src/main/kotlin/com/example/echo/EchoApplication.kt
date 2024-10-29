package com.example.echo

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
class EchoApplication

inline var <reified T> T.log : Logger
    get() = LogManager.getLogger()
    set(value) {}

fun main(args: Array<String>) {
    runApplication<EchoApplication>(*args)
}
