package com.wink.dbse.service.startup

import com.wink.dbse.eventlistener.KarmaManager
import com.wink.dbse.property.EmoteIds
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class KarmaVerifier @Autowired constructor(
    emoteIds: EmoteIds
) : ComponentDependencyVerifier(
        listOf(emoteIds.upVote, emoteIds.downVote),
        listOf(KarmaManager::class.java)
)