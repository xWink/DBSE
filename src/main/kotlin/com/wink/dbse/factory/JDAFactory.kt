package com.wink.dbse.factory

import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import com.wink.dbse.property.BotProperties
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.lang.Exception
import kotlin.system.exitProcess

@Service
class JDAFactory @Autowired constructor(
        private val prop: BotProperties,
        private val client: CommandClient,
        private val waiter: EventWaiter,
        private vararg val eventListeners: ListenerAdapter
) {

    private val logger: Logger = LoggerFactory.getLogger("JDAFactory")

    @Bean
    fun jda(): JDA {
        try {
            return JDABuilder.createDefault(prop.token)
                    .setBulkDeleteSplittingEnabled(false)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .addEventListeners(client)
                    .addEventListeners(waiter, *eventListeners)
                    .build()
        } catch (e: Exception) {
            logger.error("FATAL: Unable to initialize JDA. Cause: " + e.message)
            exitProcess(1)
        }
    }
}