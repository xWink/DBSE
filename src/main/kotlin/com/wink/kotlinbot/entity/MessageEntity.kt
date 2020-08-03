package com.wink.kotlinbot.entity

import javax.persistence.*

@Entity
@Table(name="testmessage")
class MessageEntity(
        @Id val messageId: Long = 0,
        val authorId: Long = 0,
        val channelId: Long = 0,
        val serverId: Long = 0,
        val timeSentMillis: Long = 0,
        @Column(length = 2000) val content: String = "",
        @Column(length = 2000) val attachment: String = ""
)