package com.wink.dbse.command

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import com.wink.dbse.service.Messenger
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.concurrent.TimeUnit

abstract class ConfirmableCommand(
        private val messenger: Messenger,
        private val prompt: String = "",
        private val waiter: EventWaiter
) : Command() {

    override fun execute(event: CommandEvent) {
        var message = ""
        if (prompt.isNotBlank()) {
            message += "$prompt\n"
        }
        messenger.sendMessage(event.channel, "${message}Say \"yes\" to confirm or anything else to cancel")

        waiter.waitForEvent(
                GuildMessageReceivedEvent::class.java,
                { it.author.idLong == event.author.idLong && it.channel.idLong == event.channel.idLong },
                { if (it.message.contentRaw == "yes") onConfirmationReceived(event) else println("wrong") },
                1,
                TimeUnit.MINUTES,
                null
        )
    }

    abstract fun onConfirmationReceived(event: CommandEvent)
}