package com.wink.dbse.command.economy

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.entity.economy.Marketplace
import com.wink.dbse.service.Messenger
import net.dv8tion.jda.api.EmbedBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.awt.Color

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
        val eb = EmbedBuilder()
        eb.setTitle("Market")
        eb.setColor(Color(38, 111, 232))
        var index = 1
        marketplace.listings.forEach { eb.addField("${index++}. ${it.getFullName()}", it.cost.toString(), false) }
        messenger.sendMessage(event.channel, eb.build())
        logger.info("Successfully executed market command by user \"${event.author.name}\"")
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(Market::class.java)
    }
}