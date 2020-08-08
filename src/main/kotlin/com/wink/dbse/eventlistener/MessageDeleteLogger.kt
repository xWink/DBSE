package com.wink.dbse.eventlistener

import com.wink.dbse.entity.MessageEntity
import com.wink.dbse.property.ChannelNames
import com.wink.dbse.repository.MessageRepository
import com.wink.dbse.service.ILoggedMessageFormatter
import com.wink.dbse.service.IMessenger
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MessageDeleteLogger @Autowired constructor(
        private val repository: MessageRepository,
        private val formatter: ILoggedMessageFormatter,
        private val messenger: IMessenger,
        private val channels: ChannelNames
) : ListenerAdapter() {

    override fun onMessageDelete(event: MessageDeleteEvent) {
        val guild = event.guild
        val deletedMessagesChannel: TextChannel = getDeletedMessagesChannel(event) ?: return
        val entity: MessageEntity = repository.findFirstByMessageId(event.messageIdLong) ?: return

        val author: String? = event.jda.getUserById(entity.authorId)?.name
        val channel: String? = guild.getTextChannelById(entity.channelId)?.name
        val content: String = entity.content + "\n" + entity.attachment

        val message: String = formatter.format(entity.timeSentSecs, channel, author, content)
        messenger.sendMessage(deletedMessagesChannel, message)
    }

    private fun getDeletedMessagesChannel(event: MessageDeleteEvent): TextChannel? {
        return try {
            // Try to find channel with name according to properties
            event.guild.getTextChannelsByName(channels.deletedMessages ?: throw Exception(), false)[0]
        } catch(e: Exception) {
            // If no such channel exists, stop trying to log this event
            event.jda.removeEventListener(this)
            null
        }
    }
}