package com.wink.dbse.eventlistener

import com.wink.dbse.entity.UserEntity
import com.wink.dbse.repository.UserRepository
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NewUserSaver @Autowired constructor(private val repository: UserRepository) : ListenerAdapter() {

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        if (repository.findById(event.user.idLong).isEmpty) {
            repository.save(UserEntity(event.user.idLong))
            logger.info("Successfully added new user \"${event.user.name}\" to user table")
        }
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(NewUserSaver::class.java)
    }
}