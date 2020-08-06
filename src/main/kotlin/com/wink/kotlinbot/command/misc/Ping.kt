package com.wink.kotlinbot.command.misc

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.kotlinbot.service.IMessenger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Ping @Autowired constructor(private val messenger: IMessenger) : Command() {

    init {
        name = "ping"
        help = "Returns the amount of latency with Discord's servers in milliseconds"
    }

    override fun execute(event: CommandEvent) {
        messenger.sendMessage(event.channel, "Pong! ${event.jda.gatewayPing} ms")
    }
}