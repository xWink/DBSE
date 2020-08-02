package com.wink.kotlinbot.command

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component

@Component
class NullCommand: Command() {
    override fun execute(event: CommandEvent?) {}
}