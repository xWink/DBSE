package com.wink.kotlinbot.eventlistener.eventlogger

import com.wink.kotlinbot.entity.MessageEntity
import com.wink.kotlinbot.repository.MessageRepository
import com.wink.kotlinbot.service.LoggedMessageFormatter
import com.wink.kotlinbot.service.MessageSender
import com.wink.kotlinbot.util.MessageConverter
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
        private val converter: MessageConverter
): ListenerAdapter() {

    override fun onMessageUpdate(event: MessageUpdateEvent) {
        try {
            val guild = event.guild
            val editedMessagesChannel = guild.getTextChannelsByName("edited-messages", false)[0]

            // Get channel
            val editedMessage: MessageEntity = converter.convert(event)
            val channel: String? = guild.getTextChannelById(editedMessage.channelId)?.name

            // Get original content
            val originalMessage: MessageEntity = repository.findFirstByMessageId(editedMessage.messageId)
            val content: String = originalMessage.content + "\n" + originalMessage.attachment

            // Log update
            event.jda.retrieveUserById(originalMessage.authorId).queue() {
                val message: String = formatter.format(editedMessage.timeSentMillis, channel, it.name, content)
                messageSender.sendMessage(editedMessagesChannel, message)
            }

            // Update message in db
            repository.updateByMessageId(originalMessage.messageId, editedMessage.timeSentMillis, editedMessage.content)
        } catch (ignored: IndexOutOfBoundsException) {}
    }
}