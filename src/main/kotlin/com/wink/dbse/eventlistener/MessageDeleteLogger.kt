package com.wink.dbse.eventlistener

import com.wink.dbse.entity.MessageEntity
import com.wink.dbse.property.ChannelIds
import com.wink.dbse.repository.MessageRepository
import com.wink.dbse.service.ILoggedMessageFormatter
import com.wink.dbse.service.IMessenger
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MessageDeleteLogger @Autowired constructor(
        private val repository: MessageRepository,
        private val formatter: ILoggedMessageFormatter,
        private val messenger: IMessenger,
        private val channels: ChannelIds
) : ListenerAdapter() {

    override fun onMessageDelete(event: MessageDeleteEvent) {
        val guild = event.guild
        val deleteMessagesChannelId: String? = channels.deletedMessages
        if (deleteMessagesChannelId == null) {
            logger.warn("DeletedMessages channel id is null. Removing ${this.javaClass.name} from event listeners.")
            event.jda.removeEventListener(this)
            return
        }

        val deletedMessagesChannel: TextChannel? = event.guild.getTextChannelById(deleteMessagesChannelId)
        if (deletedMessagesChannel == null) {
            logger.warn("No such channel with deleteMessages id. Removing ${this.javaClass.name} from event listeners.")
            event.jda.removeEventListener(this)
            return
        }

        val entity: MessageEntity = repository.findFirstByMessageId(event.messageIdLong) ?: return
        val author: String? = event.jda.getUserById(entity.authorId)?.name
        val channel: String? = guild.getTextChannelById(entity.channelId)?.name
        val content: String = entity.content + "\n" + entity.attachment

        val message: String = formatter.format(entity.timeSentSecs, channel, author, content)
        messenger.sendMessage(deletedMessagesChannel, message)
        logger.info("Successfully logged a deleted message by user \"$author\" in channel \"$channel\"")
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(MessageDeleteLogger::class.java)
    }
}