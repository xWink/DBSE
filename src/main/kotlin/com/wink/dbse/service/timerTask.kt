package com.wink.dbse.service

import com.wink.dbse.entity.UserEntity
import com.wink.dbse.property.BotProperties
import com.wink.dbse.repository.UserRepository
import net.dv8tion.jda.api.JDA
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

@Component
class TimerTaskScheduleHandler(roleExpiryHandler: RoleExpiryHandler) : ArrayList<TimerTask>() {

    private val timer = Timer()

    init {
        timer.schedule(roleExpiryHandler, TimeUnit.HOURS.toMillis(1))
    }
}

@Service
class RoleExpiryHandler(
        private val jda: JDA,
        private val botProperties: BotProperties,
        private val userRepository: UserRepository
) : TimerTask() {

    override fun run() {
        val users: List<UserEntity> = userRepository.findUserEntitiesByRoleExpiryIsBefore(LocalDateTime.now())
        users.forEach {
            val guild = jda.getGuildById(botProperties.serverId!!.toLong()) ?: return
            userRepository.setRole(it.userId, null, null)
            guild.removeRoleFromMember(it.userId, guild.getRoleById(it.purchasedRoleId!!) ?: return).queue()
        }
    }
}
