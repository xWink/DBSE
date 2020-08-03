package com.wink.kotlinbot.eventlistener

import com.jagrosh.jdautilities.command.CommandClient
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
        private val converter: MessageConverter,
        private val commandClient: CommandClient
) : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        // Do not save bot responses
        if (event.author.isBot) {
            return
        }

        // Do not save command calls
        val firstWord: String = event.message.contentRaw.split("\\s+".toRegex())[0]
        val commandNames: List<String> = commandClient.commands.map { it.name }
        if (firstWord.startsWith(commandClient.prefix) && commandNames.any { it == firstWord.substring(1) }) {
            return
        }

        val entity: MessageEntity = converter.convert(event)
        repository.save(entity)
    }
}