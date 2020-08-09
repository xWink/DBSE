package com.wink.dbse.eventlistener

import com.wink.dbse.entity.MessageEntity
import com.wink.dbse.property.ChannelIds
import com.wink.dbse.repository.MessageRepository
import com.wink.dbse.service.ILoggedMessageFormatter
import com.wink.dbse.service.IMessenger
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.StringBuilder

@Component
class MessageBulkDeleteLogger @Autowired constructor(
        private val repository: MessageRepository,
        private val formatter: ILoggedMessageFormatter,
        private val messenger: IMessenger,
        private val channels: ChannelIds
) : ListenerAdapter() {

    companion object {
        private const val MAX_MESSAGE_LENGTH = 2000
    }

    override fun onMessageBulkDelete(event: MessageBulkDeleteEvent) {
        val bulkDeletedMessagesChannel: TextChannel = getBulkDeletedMessagesChannel(event) ?: return
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
        }
    }

    private fun getBulkDeletedMessagesChannel(event: MessageBulkDeleteEvent): TextChannel? {
        return try {
            // Try to find channel with name according to properties
            event.guild.getTextChannelById(channels.bulkDeletedMessages ?: throw Exception())
        } catch(e: Exception) {
            // If no such channel exists, stop trying to log this event
            event.jda.removeEventListener(this)
            null
        }
    }
}