package com.wink.dbse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties
class DBSEApplication

fun main(args: Array<String>) {
    runApplication<DBSEApplication>(*args)
}
