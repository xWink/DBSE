package com.wink.dbse.eventlistener

import com.wink.dbse.property.ChannelIds
import com.wink.dbse.property.EmoteIds
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ChannelOptionsReactionListener @Autowired constructor(
        private val channelIds: ChannelIds,
        private val emoteIds: EmoteIds
) : ListenerAdapter() {

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (event.channel.name != channelIds.channelOptions || event.reactionEmote.id != emoteIds.confirm) {
            return
        }


    }

    override fun onMessageReactionRemove(event: MessageReactionRemoveEvent) {
        if (event.channel.name != channelIds.channelOptions || event.reactionEmote.id != emoteIds.confirm) {
            return
        }


    }
}