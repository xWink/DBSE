package com.wink.dbse.eventlistener

import com.wink.dbse.property.EmoteIds
import com.wink.dbse.repository.UserRepository
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class KarmaManager @Autowired constructor(
        private val emoteIds: EmoteIds,
        private val repository: UserRepository
) : ListenerAdapter() {

    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        if (emoteIds.upVote == null || emoteIds.downVote == null) {
            event.jda.removeEventListener(this)
            logger.warn("Missing upvote or downvote emote. Removing ${this.javaClass.name} from event listeners.")
            return
        }

        val emoteId: String = event.reactionEmote.id
        if (emoteId != emoteIds.upVote && emoteId != emoteIds.downVote) {
            return
        }

        // Can't upvote or downvote yourself
        val targetUser: User = event.channel.retrieveMessageById(event.messageIdLong).complete().author
        if (event.userIdLong == targetUser.idLong) {
            return
        }

        val action: String = if (emoteId == emoteIds.upVote) {
            repository.addUpVote(targetUser.idLong)
            "upvote"
        } else {
            repository.addDownVote(targetUser.idLong)
            "downvote"
        }
        logger.info("Successfully added $action to user \"${targetUser.name}\" from user \"${event.user.name}\"")
    }

    override fun onGuildMessageReactionRemove(event: GuildMessageReactionRemoveEvent) {
        val emoteId: String = event.reactionEmote.id
        if (emoteId != emoteIds.upVote && emoteId != emoteIds.downVote) {
            return
        }

        // Can't upvote or downvote yourself
        val targetUser: User = event.channel.retrieveMessageById(event.messageIdLong).complete().author
        if (event.userIdLong == targetUser.idLong) {
            return
        }

        val action: String = if (emoteId == emoteIds.upVote) {
            repository.removeUpVote(targetUser.idLong)
            "upvote"
        } else {
            repository.removeDownVote(targetUser.idLong)
            "downvote"
        }
        logger.info("Successfully removed $action from user \"${targetUser.name}\" by user \"${event.user?.name}\"")
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(KarmaManager::class.java)
    }
}