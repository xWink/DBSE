package com.wink.dbse.eventlistener

import com.wink.dbse.entity.MessageEntity
import com.wink.dbse.property.ChannelIds
import com.wink.dbse.repository.MessageRepository
import com.wink.dbse.service.LoggedMessageFormatter
import com.wink.dbse.service.Messenger
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.lang.StringBuilder

@Component
class MessageBulkDeleteLogger(
        private val repository: MessageRepository,
        private val formatter: LoggedMessageFormatter,
        private val messenger: Messenger,
        private val channels: ChannelIds
) : ListenerAdapter() {

    override fun onMessageBulkDelete(event: MessageBulkDeleteEvent) {
        val bulkDeletedMessagesChannel: TextChannel? = event.guild.getTextChannelById(channels.bulkDeletedMessages!!)
        if (bulkDeletedMessagesChannel == null) {
            logger.warn("No such channel with bulkDeletedMessages id. Removing ${this.javaClass.name} from event listeners.")
            event.jda.removeEventListener(this)
            return
        }

        val title = "**__BULK DELETE__**\n"
        val sb = StringBuilder(title)
        val deletedMessages: List<MessageEntity> = repository.findByMessageIdIn(event.messageIds.map { it.toLong() })

        for (message in deletedMessages) {
            val authorName: String = event.jda.getUserById(message.authorId)?.name ?: "Unknown Author"
            val channelName: String = event.guild.getTextChannelById(message.channelId)?.name ?: "Unknown Channel"
            val formattedMessage: String = formatter.format(message.timeSentSecs, channelName, authorName, message.content)

            // Avoid exceeding the maximum message length in discord or the output looks ugly
            if (sb.length + message.attachment.length + formattedMessage.length >= MAX_MESSAGE_LENGTH) {
                messenger.sendMessage(bulkDeletedMessagesChannel, sb.toString())
                sb.clear()
            }
            // Make sure attachments appear at the bottom of their message
            else if (message.attachment.isNotEmpty()) {
                sb.append(formattedMessage + "\n" + message.attachment)
                messenger.sendMessage(bulkDeletedMessagesChannel, sb.toString())
                sb.clear()
            }
            sb.append(formattedMessage + "\n")
        }

        if (sb.length > title.length) {
            messenger.sendMessage(bulkDeletedMessagesChannel, sb.toString())
            logger.info("Successfully logged a bulk deletion of ${deletedMessages.size} messages " +
                    "in channel \"${event.channel.name}\"")
        }
    }

    private companion object {
        private const val MAX_MESSAGE_LENGTH = 2000

        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(MessageBulkDeleteLogger::class.java)
    }
}