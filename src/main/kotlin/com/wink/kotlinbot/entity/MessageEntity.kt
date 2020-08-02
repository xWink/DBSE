package com.wink.kotlinbot.entity

import javax.persistence.*

@Entity
@Table(name="testmessage")
class MessageEntity(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private val id: Long? = null,
        private val messageId: Long = 0,
        private val authorId: Long = 0,
        private val channelId: Long = 0,
        private val serverId: Long = 0,
        private val timeSentMillis: Long = 0,
        @Column(length = 2000) private val content: String = "",
        @Column(length = 2000) private val attachment: String = ""
)