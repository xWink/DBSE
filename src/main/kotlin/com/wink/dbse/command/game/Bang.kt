package com.wink.dbse.command.game

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.service.Messenger
import org.springframework.stereotype.Component

@Component
class Bang(private val messenger: Messenger) : Command() {

    override fun execute(event: CommandEvent) {

    }
}