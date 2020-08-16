package com.wink.dbse.service.startup

import com.wink.dbse.eventlistener.MessageDeleteLogger
import com.wink.dbse.property.ChannelIds
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DeleteLoggerVerifier @Autowired constructor(
        channelIds: ChannelIds
) : ComponentDependencyVerifier(
        listOf(channelIds.deletedMessages),
        listOf(MessageDeleteLogger::class.java)
)