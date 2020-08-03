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
            val editedMessages = guild.getTextChannelsByName("edited-messages", false)[0]

            val editedMessageEntity: MessageEntity = converter.convert(event)
            val originalMessageEntities: List<MessageEntity> = repository.findByMessageIdOrderByTimeSent(editedMessageEntity.messageId)
            val originalMessageEntity: MessageEntity = originalMessageEntities.last()

            event.jda.retrieveUserById(originalMessageEntity.authorId).queue() {
                val channel: String? = guild.getTextChannelById(editedMessageEntity.channelId)?.name
                val content: String = originalMessageEntity.content + "\n" + originalMessageEntity.attachment
                val message: String = formatter.format(editedMessageEntity.timeSentMillis, channel, it.name, content)
                messageSender.sendMessage(editedMessages, message)
            }

            repository.save(editedMessageEntity)
        } catch (ignored: IndexOutOfBoundsException) {}
    }
}