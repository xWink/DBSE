package com.wink.dbse.command.admin

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.extension.attachmentProxy
import com.wink.dbse.service.Messenger
import net.dv8tion.jda.api.Permission
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class Echo(private val messenger: Messenger) : Command() {

    init {
        name = "echo"
        help = "Deletes your command message and repeats it (excluding the command name)"
        arguments = "<message to repeat>"
        userPermissions = arrayOf(Permission.ADMINISTRATOR)
    }

    override fun execute(event: CommandEvent) {
        event.message.delete().queue()

        val channel = event.channel
        val args = event.args

        messenger.sendMessage(channel, args + event.message.attachmentProxy())
        logger.info("Successfully executed echo command by user \"${event.author.name}\" " +
                "with message \"$args\" in channel \"${channel.name}\"")
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(Echo::class.java)
    }
}