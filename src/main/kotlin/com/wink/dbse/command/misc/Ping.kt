package com.wink.dbse.command.misc

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.service.IMessenger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Ping @Autowired constructor(private val messenger: IMessenger) : Command() {

    init {
        name = "ping"
        help = "Returns the amount of latency with Discord's servers in milliseconds"
    }

    override fun execute(event: CommandEvent) {
        val ping: Long = event.jda.gatewayPing
        messenger.sendMessage(event.channel, "Pong! $ping ms")
        logger.info("Successfully executed ping command by user \"${event.author.name}\" " +
                "with result \"$ping\" in channel \"${event.channel.name}\"")
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(Ping::class.java)
    }
}