package com.wink.dbse.service

import com.wink.dbse.entity.UserEntity
import com.wink.dbse.eventlistener.*
import com.wink.dbse.property.ChannelIds
import com.wink.dbse.property.EmoteIds
import com.wink.dbse.property.RoleIds
import com.wink.dbse.repository.UserRepository
import net.dv8tion.jda.api.JDA
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.function.Consumer

/**
 * Service whose run method is executed after the Bot is instantiated.
 */
interface StartupService : Consumer<JDA>

@Component
class StartupServiceList(vararg startupServices: StartupService) : ArrayList<StartupService>() {

    init {
        addAll(startupServices)
    }
}

/**
 * Checks if any of the property strings that a listener depends on are null.
 * If any are null, then all passed in dependent listeners are removed from the JDA.
 */
abstract class ComponentDependencyVerifier(
        private val dependencies: List<String?>,
        private val dependents: List<Class<*>>
) : StartupService {

    final override fun accept(jda: JDA) {
        if (dependencies.any { it == null }) {
            val toRemove = jda.eventManager.registeredListeners.find { it::class.java in dependents }
            jda.removeEventListener(toRemove)
            dependents.forEach { logger.warn("$it failed verification and was removed") }
        }
        logger.info("${this::class.java} finished verification")
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(ComponentDependencyVerifier::class.java)
    }
}


@Service
class BulkDeleteLoggerVerifier @Autowired constructor(
        channelIds: ChannelIds
) : ComponentDependencyVerifier(
        listOf(channelIds.bulkDeletedMessages),
        listOf(MessageBulkDeleteLogger::class.java)
)


@Service
class ChannelOptionsVerifier @Autowired constructor(
        emoteIds: EmoteIds,
        channelIds: ChannelIds
) : ComponentDependencyVerifier(
        listOf(emoteIds.confirm, channelIds.channelOptions),
        listOf(ChannelOptionReactionAdder::class.java, ChannelOptionsRoleManager::class.java)
)


@Service
class DeleteLoggerVerifier @Autowired constructor(
        channelIds: ChannelIds
) : ComponentDependencyVerifier(
        listOf(channelIds.deletedMessages),
        listOf(MessageDeleteLogger::class.java)
)


@Service
class KarmaVerifier @Autowired constructor(
        emoteIds: EmoteIds
) : ComponentDependencyVerifier(
        listOf(emoteIds.upVote, emoteIds.downVote),
        listOf(KarmaManager::class.java)
)


@Service
class UpdateLoggerVerifier @Autowired constructor(
        channelIds: ChannelIds
) : ComponentDependencyVerifier(
        listOf(channelIds.editedMessages),
        listOf(MessageUpdateLogger::class.java)
)


@Service
class WelcomeMessageVerifier @Autowired constructor(
        channelIds: ChannelIds,
        emoteIds: EmoteIds,
        roleIds: RoleIds
) : ComponentDependencyVerifier(
        listOf(channelIds.welcome, emoteIds.confirm, roleIds.welcome),
        listOf(WelcomeMessageReactionListener::class.java)
)


@Service
class MemberSavingService @Autowired constructor(private val repository: UserRepository) : StartupService {

    /**
     * Saves all cached members as users in the database.
     */
    override fun accept(jda: JDA) {
        jda.users.forEach { if (repository.findById(it.idLong).isEmpty) repository.save(UserEntity(it.idLong)) }
    }
}
