package com.wink.dbse.command.admin

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.property.ChannelIds
import com.wink.dbse.service.Messenger
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.TextChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import java.nio.charset.Charset

@Component
class ShowChannelOptions(
        private val messenger: Messenger,
        private val channelIds: ChannelIds
) : Command() {

    @Value("classpath:channelOptions.txt")
    private val resource: Resource? = null

    init {
        name = "showchanneloptions"
        help = "Dumps the contents of the channels list into the channelOptions channel"
        userPermissions = arrayOf(Permission.ADMINISTRATOR)
    }

    override fun execute(event: CommandEvent) {
        if (resource == null) {
            logger.warn("ChannelOptions resource is missing. Removing ${this.javaClass.name} from event listeners.")
            event.jda.removeEventListener(this)
            return
        }

        val channelId: String? = channelIds.channelOptions
        if (channelId == null) {
            logger.warn("ChannelOptions channel id is null. Removing ${this.javaClass.name} from event listeners.")
            event.jda.removeEventListener(this)
            return
        }

        val channel: TextChannel? = event.guild.getTextChannelById(channelId)
        if (channel == null) {
            logger.warn("No such channel with channelOptions id. Removing ${this.javaClass.name} from event listeners.")
            event.jda.removeEventListener(this)
            return
        }

        resource.inputStream.readBytes()
                .toString(Charset.defaultCharset())
                .lines()
                .forEach { messenger.sendMessage(channel, it.replace("\\n", "\n")) }

        logger.info("Successfully executed showChannelOptions command by user \"${event.author.name}\" " +
                "in channel \"${event.channel.name}\"")
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(ShowChannelOptions::class.java)
    }
}