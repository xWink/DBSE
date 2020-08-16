package com.wink.dbse.service.startup

import com.wink.dbse.eventlistener.WelcomeMessageReactionListener
import com.wink.dbse.property.ChannelIds
import com.wink.dbse.property.EmoteIds
import com.wink.dbse.property.RoleIds
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WelcomeMessageVerifier @Autowired constructor(
        channelIds: ChannelIds,
        emoteIds: EmoteIds,
        roleIds: RoleIds
) : ComponentDependencyVerifier(
        listOf(channelIds.welcome, emoteIds.confirm, roleIds.welcome),
        listOf(WelcomeMessageReactionListener::class.java)
)