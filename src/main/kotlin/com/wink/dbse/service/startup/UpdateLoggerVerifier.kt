package com.wink.dbse.service.startup

import com.wink.dbse.eventlistener.MessageUpdateLogger
import com.wink.dbse.property.ChannelIds
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UpdateLoggerVerifier @Autowired constructor(
        channelIds: ChannelIds
) : ComponentDependencyVerifier(
        listOf(channelIds.editedMessages),
        listOf(MessageUpdateLogger::class.java)
)