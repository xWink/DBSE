package com.wink.dbse.command.misc

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.service.IMessenger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import kotlin.random.Random.Default.nextBoolean

@Component
class Flip @Autowired constructor(private val messenger: IMessenger) : Command() {

    @Value("classpath:loonie_heads.png")
    private val heads: Resource? = null

    @Value("classpath:loonie_tails.png")
    private val tails: Resource? = null

    init {
        name = "flip"
        help = "Flips a coin and shows the result"
    }

    override fun execute(event: CommandEvent) {
        val isHeads = nextBoolean()
        val message = if (isHeads) "Heads!" else "Tails!"
        val file = if (isHeads) heads else tails
        if (file == null) {
            messenger.sendMessage(event.channel, message)
        } else {
            messenger.sendMessage(event.channel, message, file.file.readBytes(), "coin.png")
        }
    }
}