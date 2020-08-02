package com.wink.kotlinbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties
class KotlinBotApplication

fun main(args: Array<String>) {
    runApplication<KotlinBotApplication>(*args)
}
