package com.wink.dbse.command.economy

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.entity.economy.Marketplace
import com.wink.dbse.service.Messenger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class Market(
        private val messenger: Messenger,
        private val marketplace: Marketplace
) : Command() {

    init {
        name = "market"
        help = "Shows the available items on the market"
    }

    override fun execute(event: CommandEvent) {
        messenger.sendMessage(event.channel, marketplace.toString())
        logger.info("Successfully executed market command by user \"${event.author.name}\"")
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(Market::class.java)
    }
}