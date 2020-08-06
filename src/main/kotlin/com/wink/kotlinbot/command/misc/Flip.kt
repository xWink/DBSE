package com.wink.kotlinbot.command.misc

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.kotlinbot.service.IMessageSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.random.Random.Default.nextBoolean

@Component
class Flip @Autowired constructor(private val messageSender: IMessageSender) : Command() {

    init {
        name = "flip"
        help = "Flips a coin and shows its top face"
    }

    override fun execute(event: CommandEvent) {
        val isHeads = nextBoolean()
        val message = if (isHeads) "Heads!" else ("Tails!")
        val file: String = if (isHeads) "loonie_heads.png" else "loonie_tails.png"

        val image: BufferedImage = ImageIO.read(javaClass.classLoader.getResourceAsStream(file))
        val os = ByteArrayOutputStream()
        ImageIO.write(image, "png", os)

        messageSender.sendMessage(event.channel, message, os.toByteArray(), "coin.png")
    }
}