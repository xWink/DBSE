package com.wink.kotlinbot.repository

import com.wink.kotlinbot.entity.MessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository: JpaRepository<MessageEntity, Long>