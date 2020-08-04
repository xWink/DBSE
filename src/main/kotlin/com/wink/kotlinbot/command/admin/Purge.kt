package com.wink.kotlinbot.command.admin

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission
import org.springframework.stereotype.Component

@Component
class Purge : Command() {

    init {
        name = "purge"
        help = "Deletes the requested number of messages (max 99) + the command message"
        arguments = "<number of messages to purge>"
        userPermissions = arrayOf(Permission.ADMINISTRATOR)
    }

    override fun execute(event: CommandEvent) {
        try {
            val channel = event.channel
            val numMessages = event.args.toInt() + 1
            channel.history.retrievePast(numMessages).queue { channel.purgeMessages(it) }
        } catch (ignored: NumberFormatException) {}
    }
}