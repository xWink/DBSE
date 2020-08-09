package com.wink.dbse.eventlistener

import com.wink.dbse.property.ChannelIds
import com.wink.dbse.property.EmoteIds
import com.wink.dbse.property.RoleIds
import com.wink.dbse.service.impl.ChannelNameConvertingService
import com.wink.dbse.service.impl.PrivateChannelCreationService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicReference

@Component
class ChannelOptionsReactionListener @Autowired constructor(
        private val channelIds: ChannelIds,
        private val roleIds: RoleIds,
        private val emoteIds: EmoteIds,
        private val service: PrivateChannelCreationService,
        private val converter: ChannelNameConvertingService
) : ListenerAdapter() {

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (event.channel.id != channelIds.channelOptions || event.reactionEmote.id != emoteIds.confirm) {
            return
        }

        val guild = event.guild
        val content: String = event.channel.retrieveMessageById(event.messageIdLong).complete().contentRaw

        // Get corresponding role
        val role: Role = guild.getRolesByName(content, true)
                .getOrElse(0) { guild.createRole().setName(content).complete() }

        // If the role is not specifically for utility (All/Notify)
        if (roleIds.globalAccess?.any { guild.getRoleById(it)?.name == content } != true
                && guild.getRoleById(roleIds.notify ?: "")?.name != content) {

            // Create the corresponding channel if it doesn't exist
            val channelName = converter.convert(content)
            guild.getTextChannelsByName(channelName, false)
                    .ifEmpty { service.createPrivateChannel(guild, channelName, role) }
        }

        // If the user is not a bot, give them the role
        if (event.user?.isBot == false) {
            guild.addRoleToMember(event.userIdLong, role).queue()
        }
    }

    override fun onMessageReactionRemove(event: MessageReactionRemoveEvent) {
        if (event.channel.id != channelIds.channelOptions || event.reactionEmote.id != emoteIds.confirm) {
            return
        }

        val guild = event.guild
        val content: String = event.channel.retrieveMessageById(event.messageIdLong).complete().contentRaw
        val role: Role = guild.getRolesByName(content, true).getOrNull(0) ?: return

        guild.removeRoleFromMember(event.member ?: return, role).queue()
    }
}