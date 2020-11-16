package com.wink.dbse.eventlistener

import com.wink.dbse.entity.MessageEntity
import com.wink.dbse.property.ChannelIds
import com.wink.dbse.repository.MessageRepository
import com.wink.dbse.service.LoggedMessageFormatter
import com.wink.dbse.service.Messenger
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MessageDeleteLogger(
        private val repository: MessageRepository,
        private val formatter: LoggedMessageFormatter,
        private val messenger: Messenger,
        private val channels: ChannelIds
) : ListenerAdapter() {

    override fun onMessageDelete(event: MessageDeleteEvent) {
        val guild = event.guild

        val deletedMessagesChannel: TextChannel? = event.guild.getTextChannelById(channels.deletedMessages!!)
        if (deletedMessagesChannel == null) {
            logger.warn("No such channel with deleteMessages id. Removing ${this.javaClass.name} from event listeners.")
            event.jda.removeEventListener(this)
            return
        }

        val entity: MessageEntity = repository.findFirstByMessageId(event.messageIdLong) ?: return
        val user: User = event.jda.getUserById(entity.authorId) ?: return
        val userString: String =
                if (deletedMessagesChannel.members.contains(guild.getMember(user))) user.name
                else user.asMention
        val channel: TextChannel = guild.getTextChannelById(entity.channelId) ?: return
        val content: String = entity.content + "\n" + entity.attachment

        val message: String = formatter.format(entity.timeSentSecs, channel.asMention, userString, content)

        messenger.sendMessage(deletedMessagesChannel, message)
        logger.info("Successfully logged a deleted message by user \"${user.name}\" in channel \"${channel.name}\"")
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(MessageDeleteLogger::class.java)
    }
}