package com.wink.kotlinbot.repository

import com.wink.kotlinbot.entity.MessageEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface MessageRepository: JpaRepository<MessageEntity, Long> {

    fun findFirstByMessageId(id: Long): MessageEntity?

    fun findByMessageIdIn(messageIds: List<Long>): List<MessageEntity>

    @Modifying
    @Transactional
    @Query("update MessageEntity m set m.timeSentMillis = ?2, m.content = ?3 where m.messageId = ?1")
    fun updateByMessageId(messageId: Long, timeSentMillis: Long, content: String)
}