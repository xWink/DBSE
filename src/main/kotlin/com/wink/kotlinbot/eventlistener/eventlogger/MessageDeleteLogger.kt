package com.wink.kotlinbot.eventlistener.eventlogger

import com.wink.kotlinbot.entity.MessageEntity
import com.wink.kotlinbot.repository.MessageRepository
import com.wink.kotlinbot.service.LoggedMessageFormatter
import com.wink.kotlinbot.service.MessageSender
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MessageDeleteLogger @Autowired constructor(
        private val repository: MessageRepository,
        private val formatter: LoggedMessageFormatter,
        private val messageSender: MessageSender
): ListenerAdapter() {

    override fun onMessageDelete(event: MessageDeleteEvent) {
        try {
            val guild = event.guild
            val deletedMessagesChannel = guild.getTextChannelsByName("deleted-messages", false)[0]
            val entity: MessageEntity = repository.findFirstByMessageId(event.messageIdLong)

            event.jda.retrieveUserById(entity.authorId).queue() {
                val channel: String? = guild.getTextChannelById(entity.channelId)?.name
                val content: String = entity.content + "\n" + entity.attachment
                val message: String = formatter.format(entity.timeSentMillis, channel, it.name, content)
                messageSender.sendMessage(deletedMessagesChannel, message)
            }
        } catch (ignored: IndexOutOfBoundsException) {}
    }
}