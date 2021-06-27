package com.wink.dbse.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "message")
class MessageEntity(
        @Id val messageId: Long = 0,
        val authorId: Long = 0,
        val channelId: Long = 0,
        val timeSentSecs: Long = 0,
        @Column(length = 2000) val content: String = "",
        @Column(length = 2000) val attachment: String = ""
)