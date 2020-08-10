package com.wink.dbse.eventlistener

import com.wink.dbse.property.ChannelIds
import com.wink.dbse.property.EmoteIds
import com.wink.dbse.property.RoleIds
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class WelcomeMessageReactionListener(
        private val channelIds: ChannelIds,
        private val emoteIds: EmoteIds,
        private val roleIds: RoleIds
) : ListenerAdapter() {

    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        if (event.channel.id != channelIds.welcome || event.user.isBot || event.reactionEmote.id != emoteIds.confirm) {
            return
        }

        val member: Member = event.member
        val tosRole: Role = event.guild.getRoleById(roleIds.acceptedToS ?: return) ?: return

        if (!member.roles.contains(tosRole)) {
            event.guild.addRoleToMember(member, tosRole).queue()
        }
    }
}