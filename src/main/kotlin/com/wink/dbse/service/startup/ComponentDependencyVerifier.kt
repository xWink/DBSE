package com.wink.dbse.service.startup

import net.dv8tion.jda.api.JDA
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Checks if any of the property strings that a listener depends on are null.
 * If any are null, then all passed in dependent listeners are removed from the JDA.
 */
abstract class ComponentDependencyVerifier(
        private val dependencies: List<String?>,
        private val dependents: List<Class<*>>
) : IStartupService {

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