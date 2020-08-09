package com.wink.dbse.eventlistener

import com.wink.dbse.property.ChannelIds
import com.wink.dbse.property.EmoteIds
import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ChannelOptionReactionAdder @Autowired constructor(
        private val channelIds: ChannelIds,
        private val emoteIds: EmoteIds
) : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.channel.id != channelIds.channelOptions || !event.author.isBot) {
            return
        }

        val confirmEmote: Emote = event.jda.getEmoteById(emoteIds.confirm ?: return) ?: return
        if (!event.message.contentRaw.startsWith("-")) {
            event.message.addReaction(confirmEmote).queue()
        }
    }
}