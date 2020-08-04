package com.wink.kotlinbot.eventlistener.eventlogger

import com.wink.kotlinbot.entity.MessageEntity
import com.wink.kotlinbot.repository.MessageRepository
import com.wink.kotlinbot.service.LoggedMessageFormatter
import com.wink.kotlinbot.service.MessageSender
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.StringBuilder

@Component
class MessageBulkDeleteLogger @Autowired constructor(
        private val repository: MessageRepository,
        private val formatter: LoggedMessageFormatter,
        private val messageSender: MessageSender
) : ListenerAdapter() {

    companion object {
        private const val MAX_MESSAGE_LENGTH = 2000
    }

    override fun onMessageBulkDelete(event: MessageBulkDeleteEvent) {
        val deletedMessagesChannel: TextChannel = try {
            event.guild.getTextChannelsByName("deleted-messages", false)[0]
        } catch(e: IndexOutOfBoundsException) { null } ?: return

        val sb = StringBuilder("**__BULK DELETE__**\n")
        val deletedMessages: List<MessageEntity> = repository.findByMessageIdIn(event.messageIds.map { it.toLong() })

        for (message in deletedMessages) {
            val authorName: String = event.jda.getUserById(message.authorId)?.name ?: "Unknown Author"
            val channelName: String = event.guild.getTextChannelById(message.channelId)?.name ?: "Unknown Channel"
            val formattedMessage: String = formatter.format(message.timeSentMillis, channelName, authorName, message.content)

            // Avoid exceeding the maximum message length in discord or the output looks ugly
            if (sb.length + message.attachment.length + formattedMessage.length >= MAX_MESSAGE_LENGTH) {
                messageSender.sendMessage(deletedMessagesChannel, sb.toString())
                sb.clear()
            }
            // Make sure attachments appear at the bottom of their message
            else if (message.attachment.isNotEmpty()) {
                sb.append(formattedMessage + "\n" + message.attachment)
                messageSender.sendMessage(deletedMessagesChannel, sb.toString())
                sb.clear()
            }
            sb.append(formattedMessage + "\n")
        }
        messageSender.sendMessage(deletedMessagesChannel, sb.toString())
    }
}