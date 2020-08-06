package com.wink.kotlinbot.command.admin

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.kotlinbot.extension.attachmentProxy
import com.wink.kotlinbot.service.IMessenger
import net.dv8tion.jda.api.Permission
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Echo @Autowired constructor(private val messenger: IMessenger) : Command() {

    init {
        name = "echo"
        help = "Deletes your command message and repeats it (excluding the command name)"
        arguments = "<message to repeat>"
        userPermissions = arrayOf(Permission.ADMINISTRATOR)
    }

    override fun execute(event: CommandEvent) {
        event.message.delete().queue()
        messenger.sendMessage(event.channel, event.args + event.message.attachmentProxy())
    }
}