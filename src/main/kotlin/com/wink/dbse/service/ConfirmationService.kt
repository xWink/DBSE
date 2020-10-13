package com.wink.dbse.service

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.function.Predicate

@Service
class ConfirmationService(
        private val eventWaiter: EventWaiter,
        private val messenger: Messenger
) {

    fun confirm(event: CommandEvent, prompt: String = "", action: Consumer<CommandEvent>, timeoutAction: Runnable? = null) {
        var message = ""
        if (prompt.isNotBlank()) {
            message += "$prompt\n"
        }
        messenger.sendMessage(event.channel, "${message}Say \"yes\" to confirm or anything else to cancel")

        eventWaiter.waitForEvent(
                GuildMessageReceivedEvent::class.java,
                Predicate { it.author.idLong == event.author.idLong && it.channel.idLong == event.channel.idLong },
                Consumer<GuildMessageReceivedEvent> { if (it.message.contentRaw == "yes") action.accept(event) },
                1,
                TimeUnit.MINUTES,
                timeoutAction
        )
    }
}