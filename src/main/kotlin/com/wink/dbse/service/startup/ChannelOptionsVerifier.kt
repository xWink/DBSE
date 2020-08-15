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
    override fun accept(jda: JDA) {
        if (emoteIds.confirm == null || channelIds.channelOptions == null) {
            jda.eventManager.registeredListeners.forEach {
                if (it.javaClass == ChannelOptionReactionAdder::class.java
                        || it.javaClass == ChannelOptionsRoleManager::class.java) {

                    jda.removeEventListener(it)
                    logger.warn("${it::class.simpleName} failed verification and was removed")
                }
            }
        }
        logger.info("Verified ChannelOptions")
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(ChannelOptionsVerifier::class.java)
    }
}