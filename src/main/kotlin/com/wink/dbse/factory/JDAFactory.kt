package com.wink.dbse.factory

import com.wink.dbse.property.BotProperties
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import kotlin.system.exitProcess

@Service
class JDAFactory(
        private val prop: BotProperties
) {

    private val logger: Logger = LoggerFactory.getLogger("JDAFactory")

    private var jda: JDA? = null

    @Bean
    fun jda(): JDA {
        if (jda == null) {
            try {
                jda = JDABuilder.createDefault(prop.token)
                        .setBulkDeleteSplittingEnabled(false)
                        .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
                        .setMemberCachePolicy(MemberCachePolicy.ALL)
                        .build()
                jda!!.awaitReady()
            } catch (e: Exception) {
                logger.error("FATAL: Unable to initialize JDA. Cause: " + e.message)
                exitProcess(1)
            }
        }
        return jda as JDA
    }
}