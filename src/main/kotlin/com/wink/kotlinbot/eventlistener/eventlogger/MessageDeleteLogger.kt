package com.wink.kotlinbot.eventlistener.eventlogger

import com.wink.kotlinbot.entity.MessageEntity
import com.wink.kotlinbot.repository.MessageRepository
import com.wink.kotlinbot.service.ILoggedMessageFormatter
import com.wink.kotlinbot.service.IMessageSender
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MessageDeleteLogger @Autowired constructor(
        private val repository: MessageRepository,
        private val formatter: ILoggedMessageFormatter,
        private val messageSender: IMessageSender
) : ListenerAdapter() {

    override fun onMessageDelete(event: MessageDeleteEvent) {
        val guild = event.guild
        val deletedMessagesChannel: TextChannel = try {
            event.guild.getTextChannelsByName("deleted-messages", false)[0]
        } catch(e: IndexOutOfBoundsException) { null } ?: return

        val entity: MessageEntity = repository.findFirstByMessageId(event.messageIdLong) ?: return
        val author: String? = event.jda.getUserById(entity.authorId)?.name
        val channel: String? = guild.getTextChannelById(entity.channelId)?.name
        val content: String = entity.content + "\n" + entity.attachment

        val message: String = formatter.format(entity.timeSentMillis, channel, author, content)
        messageSender.sendMessage(deletedMessagesChannel, message)
    }
}