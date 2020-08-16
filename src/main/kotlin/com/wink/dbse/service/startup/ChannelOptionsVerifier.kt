package com.wink.dbse.service.startup

import com.wink.dbse.eventlistener.ChannelOptionReactionAdder
import com.wink.dbse.eventlistener.ChannelOptionsRoleManager
import com.wink.dbse.property.ChannelIds
import com.wink.dbse.property.EmoteIds
import com.wink.dbse.service.IStartupService
import net.dv8tion.jda.api.JDA
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChannelOptionsVerifier @Autowired constructor(
        emoteIds: EmoteIds,
        channelIds: ChannelIds
) : ComponentDependencyVerifier(
        listOf(emoteIds.confirm, channelIds.channelOptions),
        listOf(ChannelOptionReactionAdder::class.java, ChannelOptionsRoleManager::class.java)
)