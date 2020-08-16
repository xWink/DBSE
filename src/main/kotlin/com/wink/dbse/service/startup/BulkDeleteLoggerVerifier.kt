package com.wink.dbse.service.startup

import com.wink.dbse.eventlistener.MessageBulkDeleteLogger
import com.wink.dbse.property.ChannelIds
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BulkDeleteLoggerVerifier @Autowired constructor(
        channelIds: ChannelIds
) : ComponentDependencyVerifier(
        listOf(channelIds.bulkDeletedMessages),
        listOf(MessageBulkDeleteLogger::class.java)
)