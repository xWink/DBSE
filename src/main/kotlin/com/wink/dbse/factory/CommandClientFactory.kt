package com.wink.dbse.factory

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.wink.dbse.property.BotProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class CommandClientFactory @Autowired constructor(
        private val properties: BotProperties,
        private vararg val commands: Command
) {

    @Bean
    fun commandClient(): CommandClient = CommandClientBuilder()
            .setOwnerId(properties.ownerId)
            .setPrefix(properties.commandPrefix)
            .addCommands(*commands)
            .build()
}