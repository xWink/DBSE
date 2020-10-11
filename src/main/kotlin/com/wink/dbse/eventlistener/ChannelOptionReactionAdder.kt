package com.wink.dbse.eventlistener

import com.wink.dbse.property.ChannelIds
import com.wink.dbse.property.EmoteIds
import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
final class ChannelOptionReactionAdder(
        private val channelIds: ChannelIds,
        private val emoteIds: EmoteIds
) : ListenerAdapter() {

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.channel.id != channelIds.channelOptions || !event.author.isBot) {
            return
        }

        val emoteId: String = emoteIds.confirm!!
        val confirmEmote: Emote? = event.guild.getEmoteById(emoteId)
        if (confirmEmote == null) {
            logger.warn("No such emote with confirm id. Removing ${this.javaClass.name} from event listeners.")
            event.jda.removeEventListener(this)
            return
        }

        if (!event.message.contentRaw.startsWith("-")) {
            event.message.addReaction(confirmEmote).queue() {
                logger.info("Successfully added confirm emote to message in channelOptions channel")
            }
        }
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(ChannelOptionReactionAdder::class.java)
    }
}