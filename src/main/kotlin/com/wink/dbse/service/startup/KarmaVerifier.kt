package com.wink.dbse.service.startup

import com.wink.dbse.eventlistener.KarmaManager
import com.wink.dbse.property.EmoteIds
import com.wink.dbse.service.IStartupService
import net.dv8tion.jda.api.JDA
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class KarmaVerifier @Autowired constructor(
    emoteIds: EmoteIds
) : ComponentDependencyVerifier(
        listOf(emoteIds.upVote, emoteIds.downVote),
        listOf(KarmaManager::class.java)
)