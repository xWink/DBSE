package com.wink.dbse.command.game

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.entity.TodayBangerEntity
import com.wink.dbse.property.ChannelIds
import com.wink.dbse.repository.TodayBangerRepository
import com.wink.dbse.service.Messenger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Component

@Component
class Daily(
    private val channelIds: ChannelIds,
    private val messenger: Messenger,
    private val todayBangerRepository: TodayBangerRepository,
    private val jda: JDA
): Command() {

    private val bangChannel: TextChannel? = jda.getTextChannelById(channelIds.spamChannel ?: "")

    init {
        help = "Shows when your daily reward resets."
        name = "daily"
    }

    override fun execute(event: CommandEvent) {
        if(todayBangerRepository.findFirstByUserId(event.author.idLong) == null) {
            messenger.sendMessage(bangChannel ?: return, "Your daily reward is available now! Say `!bang`")
        } else {
            messenger.sendMessage(bangChannel ?: return, "Your next daily reward is available tomorrow")
        }
    }
}