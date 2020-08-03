package com.wink.kotlinbot.eventlistener.eventlogger

import com.wink.kotlinbot.entity.MessageEntity
import com.wink.kotlinbot.repository.MessageRepository
import com.wink.kotlinbot.service.LoggedMessageFormatter
import com.wink.kotlinbot.service.MessageSender
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MessageBulkDeleteLogger @Autowired constructor(
        private val repository: MessageRepository,
        private val formatter: LoggedMessageFormatter,
        private val messageSender: MessageSender
): ListenerAdapter() {

    override fun onMessageBulkDelete(event: MessageBulkDeleteEvent) {
        try {
            val deletedMessagesChannel = event.guild.getTextChannelsByName("deleted-messages", false)[0]
            val deletedMessages: List<MessageEntity> = repository.findByMessageIdIn(event.messageIds.map { it.toLong() })
            //TODO: gather all deleted messages into formatted strings to output
        } catch (ignored: IndexOutOfBoundsException) {}
    }
}