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
        private val emoteIds: EmoteIds,
        private val channelIds: ChannelIds
) : IStartupService {

    /**
     * Removes ChannelOptions-related event listeners if their required emotes and channels are not set.
     */
    override fun accept(jda: JDA) { // TODO: MAKE PARENT CLASS THAT DOES THIS FOR ALL THE VERIFIERS
        if (emoteIds.confirm == null || channelIds.channelOptions == null) {
            val toRemove = listOf(ChannelOptionReactionAdder::class.java, ChannelOptionsRoleManager::class.java)
            val found = jda.eventManager.registeredListeners.find { it::class.java in toRemove }
            jda.removeEventListener(found)
            toRemove.forEach { logger.warn("$it failed verification and was removed") }
        }
        logger.info("Verified ChannelOptions")
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(ChannelOptionsVerifier::class.java)
    }
}