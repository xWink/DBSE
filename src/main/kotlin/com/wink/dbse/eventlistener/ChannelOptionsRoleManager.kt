package com.wink.dbse.eventlistener

import com.wink.dbse.property.ChannelIds
import com.wink.dbse.property.EmoteIds
import com.wink.dbse.property.RoleIds
import com.wink.dbse.service.impl.ChannelNameConvertingService
import com.wink.dbse.service.impl.PrivateChannelCreationService
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
final class ChannelOptionsRoleManager @Autowired constructor(
        private val channelIds: ChannelIds,
        private val roleIds: RoleIds,
        private val emoteIds: EmoteIds,
        private val service: PrivateChannelCreationService,
        private val converter: ChannelNameConvertingService
) : ListenerAdapter() {


    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        if (event.channel.id != channelIds.channelOptions || event.reactionEmote.id != emoteIds.confirm) {
            return
        }

        val guild = event.guild
        val content: String = event.channel.retrieveMessageById(event.messageIdLong).complete().contentRaw

        // Get corresponding role
        val role: Role = guild.getRolesByName(content, true).getOrElse(0) {
            logger.info("Successfully created channelOptions role with name \"$content\"")
            guild.createRole().setName(content).complete()
        }

        // If the role is not specifically for utility (All/Notify)
        if (roleIds.globalAccess?.any { guild.getRoleById(it)?.name == content } != true
                && guild.getRoleById(roleIds.notify ?: "")?.name != content) {

            // Create the corresponding channel if it doesn't exist
            val channelName = converter.convert(content)
            guild.getTextChannelsByName(channelName, false).ifEmpty {
                logger.info("Successfully created channelOptions channel with name \"$channelName\"")
                service.createPrivateChannel(guild, channelName, role)
            }
        }

        // If the user is not a bot, give them the role
        if (!event.user.isBot) {
            guild.addRoleToMember(event.userIdLong, role).queue()
            logger.info("Successfully added channelOptions role \"$content\" to user \"${event.user.name}\"")
        }
    }

    override fun onGuildMessageReactionRemove(event: GuildMessageReactionRemoveEvent) {
        if (event.channel.id != channelIds.channelOptions || event.reactionEmote.id != emoteIds.confirm) {
            return
        }

        val guild = event.guild
        val content: String = event.channel.retrieveMessageById(event.messageIdLong).complete().contentRaw
        val role: Role = guild.getRolesByName(content, true).getOrNull(0) ?: return

        guild.removeRoleFromMember(event.userIdLong, role).queue()
        logger.info("Successfully removed channelOptions role \"$content\" from user \"${event.user?.name}\"")
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(ChannelOptionReactionAdder::class.java)
    }
}