package com.wink.dbse.eventlistener.eventlogger

import com.wink.dbse.entity.MessageEntity
import com.wink.dbse.property.ChannelNames
import com.wink.dbse.repository.MessageRepository
import com.wink.dbse.service.ILoggedMessageFormatter
import com.wink.dbse.service.IMessenger
import com.wink.dbse.service.impl.MessageConvertingService
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MessageUpdateLogger @Autowired constructor(
        private val repository: MessageRepository,
        private val formatter: ILoggedMessageFormatter,
        private val messenger: IMessenger,
        private val converter: MessageConvertingService,
        private val channels: ChannelNames
) : ListenerAdapter() {

    override fun onMessageUpdate(event: MessageUpdateEvent) {
        val editedMessage: MessageEntity = converter.convert(event)
        logOriginalMessage(event, editedMessage)
        repository.updateByMessageId(editedMessage.messageId, editedMessage.timeSentSecs, editedMessage.content)
    }

    private fun logOriginalMessage(event: MessageUpdateEvent, editedMessage: MessageEntity) {
        val editedMessagesChannel: TextChannel = getUpdatedMessagesChannel(event) ?: return
        val originalMessage: MessageEntity = repository.findFirstByMessageId(editedMessage.messageId) ?: return

        val originalContent: String = originalMessage.content + "\n" + originalMessage.attachment
        val channel: String? = event.guild.getTextChannelById(originalMessage.channelId)?.name

        event.jda.retrieveUserById(originalMessage.authorId).queue() {
            val message: String = formatter.format(originalMessage.timeSentSecs, channel, it.name, originalContent)
            messenger.sendMessage(editedMessagesChannel, message)
        }
    }

    private fun getUpdatedMessagesChannel(event: MessageUpdateEvent): TextChannel? {
        return try {
            // Try to find channel with name according to properties
            event.guild.getTextChannelsByName(channels.editedMessages ?: throw Exception(), false)[0]
        } catch(e: Exception) {
            // If no such channel exists, stop trying to log this event
            event.jda.removeEventListener(this)
            null
        }
    }
}