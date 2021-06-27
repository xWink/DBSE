package com.wink.dbse.command.game

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.repository.TodayBangerRepository
import com.wink.dbse.repository.userNotBangedToday
import com.wink.dbse.service.Messenger
import org.springframework.stereotype.Component

@Component
class Daily(
    private val messenger: Messenger,
    private val todayBangerRepository: TodayBangerRepository
): Command() {

    init {
        help = "Shows when your daily reward resets."
        name = "daily"
    }

    override fun execute(event: CommandEvent) {
        val message = if(todayBangerRepository.userNotBangedToday(event.author.idLong))
            "Your daily reward is available now! Say `!bang`" else "Your next daily reward is available tomorrow"
            messenger.sendMessage(event.channel, message)
    }
}