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
    private val emoteIds: EmoteIds
) : IStartupService {

    /**
     * Removes Karma-related event listeners if their required emotes are not set.
     */
    override fun accept(jda: JDA) {
        if (emoteIds.upVote == null || emoteIds.downVote == null) {
            val toRemove = KarmaManager::class.java
            val listener = jda.eventManager.registeredListeners.find { it::class.java == toRemove }
            jda.removeEventListener(listener)
            logger.warn("${toRemove.name} failed verification and was removed")
        }
        logger.info("Verified Karma")
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(KarmaVerifier::class.java)
    }
}