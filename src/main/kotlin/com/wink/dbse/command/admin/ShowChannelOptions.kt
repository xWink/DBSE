package com.wink.dbse.command.admin

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.property.ChannelIds
import com.wink.dbse.service.IMessenger
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

@Component
class ShowChannelOptions @Autowired constructor(
        private val messenger: IMessenger,
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
        if (resource?.file == null) {
            return
        }

        val channel: TextChannel = event.guild.getTextChannelById(channelIds.channelOptions ?: return) ?: return
        resource.file.forEachLine { messenger.sendMessage(channel, it.replace("\\n", "\n")) }
    }
}