package com.wink.kotlinbot.service

import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.utils.AttachmentOption

interface IMessenger {

    /**
     * Sends a given message to a given channel. If the message length exceeds the maximum allowed by Discord (2000),
     * the message is broken up into multiple messages which are all sent in order.
     * @param channel the channel the message should be sent to
     * @param message the message to send
     */
    fun sendMessage(channel: MessageChannel, message: String)

    /**
     * Sends a given message with a given attachment to a given channel. If the message length exceeds the maximum
     * allowed by Discord (2000), the message is broken up into multiple messages which are all sent in order.
     * The attachment is always sent with the last message.
     * @param channel the channel the message should be sent to
     * @param message the message to send
     * @param attachment the attachment to include in the message
     * @param attachmentName the name of the attachment
     * @param options options to configure the attachment
     */
    fun sendMessage(channel: MessageChannel, message: String, attachment: ByteArray, attachmentName: String, vararg options: AttachmentOption)
}