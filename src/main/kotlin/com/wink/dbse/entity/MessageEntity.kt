package com.wink.dbse.entity

import javax.persistence.*

@Entity
@Table(name="testmessage")
class MessageEntity(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        val messageId: Long = 0,
        val authorId: Long = 0,
        val channelId: Long = 0,
        val timeSentSecs: Long = 0,
        @Column(length = 2000) val content: String = "",
        @Column(length = 2000) val attachment: String = ""
)