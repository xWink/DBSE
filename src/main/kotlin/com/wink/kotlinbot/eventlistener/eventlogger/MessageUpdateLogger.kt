package com.wink.kotlinbot.eventlistener.eventlogger

import com.wink.kotlinbot.entity.MessageEntity
import com.wink.kotlinbot.repository.MessageRepository
import com.wink.kotlinbot.service.LoggedMessageFormatter
import com.wink.kotlinbot.service.MessageSender
import com.wink.kotlinbot.service.impl.MessageConvertingService
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.IndexOutOfBoundsException

@Component
class MessageUpdateLogger @Autowired constructor(
        private val repository: MessageRepository,
        private val formatter: LoggedMessageFormatter,
        private val messageSender: MessageSender,
        private val converter: MessageConvertingService
) : ListenerAdapter() {

    override fun onMessageUpdate(event: MessageUpdateEvent) {
        val editedMessage: MessageEntity = converter.convert(event)
        logOriginalMessage(event, editedMessage)
        repository.updateByMessageId(editedMessage.messageId, editedMessage.timeSentMillis, editedMessage.content)
    }

    private fun logOriginalMessage(event: MessageUpdateEvent, editedMessage: MessageEntity) {
        val editedMessagesChannel: TextChannel = try {
            event.guild.getTextChannelsByName("edited-messages", false)[0]
        } catch(e: IndexOutOfBoundsException) { null } ?: return

        val originalMessage: MessageEntity = repository.findFirstByMessageId(editedMessage.messageId) ?: return
        val originalContent: String = originalMessage.content + "\n" + originalMessage.attachment
        val channel: String? = event.guild.getTextChannelById(originalMessage.channelId)?.name

        event.jda.retrieveUserById(originalMessage.authorId).queue() {
            val message: String = formatter.format(originalMessage.timeSentMillis, channel, it.name, originalContent)
            messageSender.sendMessage(editedMessagesChannel, message)
        }
    }
}