package com.wink.dbse.command.economy

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.oauth2.exceptions.InvalidStateException
import com.wink.dbse.entity.UserEntity
import com.wink.dbse.entity.economy.Marketplace
import com.wink.dbse.entity.economy.RoleListing
import com.wink.dbse.property.BotProperties
import com.wink.dbse.repository.UserRepository
import com.wink.dbse.service.EconomyService
import com.wink.dbse.service.Messenger
import net.dv8tion.jda.api.entities.MessageChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class Buy(
        private val messenger: Messenger,
        private val botProperties: BotProperties,
        private val marketplace: Marketplace,
        private val userRepository: UserRepository,
        private val economyService: EconomyService
) : Command() {

    init {
        name = "buy"
        help = "Purchase an item from the market"
        arguments = "<id number of item to buy>"
    }

    override fun execute(event: CommandEvent) {
        val listingId = event.args.toIntOrNull()
        if (listingId == null || listingId < 1 || listingId > marketplace.listings.size) {
            sendHelp(event.channel)
            return
        }

        val user: UserEntity = userRepository.findById(event.author.idLong).get()
        val listing: RoleListing = marketplace.listings[listingId - 1]
        try {
            economyService.buy(user.userId, listing, event.guild)
            messenger.sendMessage(event.channel, "Enjoy your new role!")
        } catch (e: InvalidStateException) {
            messenger.sendMessage(event.channel, e.message.toString())
            return
        }

        logger.info("Successfully executed buy command by user \"${event.author.name}\" " +
                "for Role Listing \"$listingId\"")
    }

    private fun sendHelp(channel: MessageChannel) {
        val call: String = botProperties.commandPrefix + name
        val errorOut = "Expected: $call $arguments\nExample: `$call 1`"
        messenger.sendMessage(channel, errorOut)
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(Buy::class.java)
    }
}
