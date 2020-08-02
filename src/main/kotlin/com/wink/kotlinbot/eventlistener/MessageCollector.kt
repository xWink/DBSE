package com.wink.kotlinbot.eventlistener

import com.wink.kotlinbot.entity.MessageEntity
import com.wink.kotlinbot.util.MessageConverter
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.wink.kotlinbot.repository.MessageRepository
import javax.transaction.Transactional

@Component
@Transactional
class MessageCollector @Autowired constructor(
        private val repository: MessageRepository,
        private val converter: MessageConverter
) : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!event.author.isBot) {
            val entity: MessageEntity = converter.convert(event)
            repository.save(entity)
        }
    }
}