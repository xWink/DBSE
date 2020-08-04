package com.wink.kotlinbot.eventlistener.eventlogger

import com.wink.kotlinbot.entity.MessageEntity
import com.wink.kotlinbot.repository.MessageRepository
import com.wink.kotlinbot.service.LoggedMessageFormatter
import com.wink.kotlinbot.service.MessageSender
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MessageDeleteLogger @Autowired constructor(
        private val repository: MessageRepository,
        private val formatter: LoggedMessageFormatter,
        private val messageSender: MessageSender
) : ListenerAdapter() {

    override fun onMessageDelete(event: MessageDeleteEvent) {
        val guild = event.guild
        val deletedMessagesChannel: TextChannel = try {
            event.guild.getTextChannelsByName("deleted-messages", false)[0]
        } catch(e: IndexOutOfBoundsException) { null } ?: return

        val Entity: MessageEntity = repository.findFirstByMessageId(event.messageIdLong) ?: return
        event.jda.retrieveUserById(Entity.authorId).queue() {
            val channel: String? = guild.getTextChannelById(Entity.channelId)?.name
            val content: String = Entity.content + "\n" + Entity.attachment
            val message: String = formatter.format(Entity.timeSentMillis, channel, it.name, content)
            messageSender.sendMessage(deletedMessagesChannel, message)
        }
    }
}