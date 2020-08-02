package com.wink.kotlinbot.repository

import com.wink.kotlinbot.entity.MessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository: JpaRepository<MessageEntity, Long> {

    @Query("SELECT m FROM MessageEntity m WHERE m.messageId = ?1 ORDER BY m.timeSentMillis")
    fun findByMessageIdOrderByTimeSent(id: Long): List<MessageEntity>
}