package com.wink.dbse.command.admin

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class Purge : Command() {

    init {
        name = "purge"
        help = "Deletes the requested number of messages (max 99) + the command message"
        arguments = "<number of messages to purge>"
        userPermissions = arrayOf(Permission.MANAGE_CHANNEL)
    }

    override fun execute(event: CommandEvent) {
        try {
            val channel = event.channel
            val numMessages = event.args.toInt() + 1
            channel.history.retrievePast(numMessages).queue { channel.purgeMessages(it) }
            logger.info("Successfully executed purge command by user \"${event.author.name}\" " +
                    "on ${numMessages - 1} messages in channel \"${channel.name}\"")
        } catch (ignored: NumberFormatException) {}
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(Purge::class.java)
    }
}