package com.wink.kotlinbot.service.impl

import com.wink.kotlinbot.entity.MessageEntity
import com.wink.kotlinbot.extension.attachmentProxy
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.GenericMessageEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.time.OffsetDateTime

@Component
class MessageConvertingService : Converter<GenericMessageEvent, MessageEntity> {

    override fun convert(source: GenericMessageEvent): MessageEntity {
        return when (source) {
            is MessageReceivedEvent -> convert(source)
            is MessageUpdateEvent -> convert(source)
            else -> throw IllegalArgumentException("Only supports MessageReceivedEvent and MessageUpdateEvent")
        }
    }

    private fun convert(source: MessageReceivedEvent): MessageEntity {
        return convert(source.message, source.message.timeCreated.toEpochSecond() * 1000L)
    }

    private fun convert(source: MessageUpdateEvent): MessageEntity {
        val message: Message = source.message
        val timeEdited: OffsetDateTime = message.timeEdited!!
        return convert(message, timeEdited.toEpochSecond() * 1000L)
    }

    private fun convert(message: Message, timeSentMillis: Long): MessageEntity {
        return MessageEntity(
                messageId = message.idLong,
                authorId = message.author.idLong,
                channelId = message.channel.idLong,
                timeSentMillis = timeSentMillis,
                content = message.contentRaw,
                attachment = message.attachmentProxy())
    }
}