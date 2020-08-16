package com.wink.dbse.command.misc

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.service.messenger.IMessenger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
        val resource = if (isHeads) heads else tails
        if (resource == null) {
            messenger.sendMessage(event.channel, message)
        } else {
            messenger.sendMessage(event.channel, message, resource.inputStream.readBytes(), "coin.png")
        }
        logger.info("Successfully executed flip command by user \"${event.author.name}\" " +
                "with result \"$message\" in channel \"${event.channel.name}\"")
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(Flip::class.java)
    }
}