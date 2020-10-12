package com.wink.dbse.command.economy

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.property.BotProperties
import com.wink.dbse.service.EconomyService
import com.wink.dbse.service.Messenger
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.awt.Color

@Component
class Gift(
        private val messenger: Messenger,
        private val economyService: EconomyService,
        private val botProperties: BotProperties
) : Command() {

    init {
        name = "gift"
        help = "send a gift of gc to another user"
        arguments = "<@user to send a gift> <amount of gc>"
    }

    override fun execute(event: CommandEvent) {
        try {
            val receiver = event.message.mentionedMembers[0].user
            if (receiver.idLong == event.author.idLong) {
                messenger.sendMessage(event.channel, "You cannot gift yourself")
                return
            }

            val amount = event.args.split(" ")[1].toInt()
            if (amount <= 0) {
                messenger.sendMessage(event.channel, "You must gift a positive amount")
                return
            }

            economyService.gift(event.author.idLong, receiver.idLong, amount)
            messenger.sendMessage(event.channel, createGiftEmbed(event.author, receiver, amount))
        } catch (e: Exception) {
            sendHelp(event.channel)
        }
    }

    private fun createGiftEmbed(sender: User, receiver: User, amount: Int): MessageEmbed {
        val eb = EmbedBuilder()
        eb.setColor(Color.YELLOW)
        eb.setAuthor("Gift Successfully Sent!", sender.avatarUrl, sender.avatarUrl)
        eb.setTitle("Sent $amount gc to ${receiver.name}!")
        eb.setThumbnail(receiver.avatarUrl)
        return eb.build()
    }

    private fun sendHelp(channel: MessageChannel) {
        val call: String = botProperties.commandPrefix + name
        val errorOut = "Expected: $call $arguments\nExample: `$call @Friend 10`"
        messenger.sendMessage(channel, errorOut)
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(Gift::class.java)
    }
}