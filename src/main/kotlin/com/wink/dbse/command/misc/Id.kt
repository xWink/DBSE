package com.wink.dbse.command.misc

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.property.BotProperties
import com.wink.dbse.service.messenger.IMessenger
import net.dv8tion.jda.api.entities.MessageChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Id @Autowired constructor(
        private val messenger: IMessenger,
        private val botProperties: BotProperties
) : Command() {

    init {
        name = "id"
        help = "Provides the name of a user with a corresponding ID"
        arguments = "<ID number of the user>"
    }

    override fun execute(event: CommandEvent) {
        val id: Long? = event.args.toLongOrNull()
        if (id == null) {
            sendHelp(event.channel)
            return
        }

        var out: String? = event.jda.getUserById(id)?.name
        out = if (out != null) "Found user: $out" else "Could not find a user with that ID"
        messenger.sendMessage(event.channel, out)
        logger.info("Successfully executed id command by user \"${event.author.name}\" " +
                "for id \"$id\" in channel \"${event.channel.name}\"")
    }

    private fun sendHelp(channel: MessageChannel) {
        val call: String = botProperties.commandPrefix + name
        val errorOut = "Expected: $call $arguments\nExample: `$call 192882738527731712`"
        messenger.sendMessage(channel, errorOut)
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(Id::class.java)
    }
}