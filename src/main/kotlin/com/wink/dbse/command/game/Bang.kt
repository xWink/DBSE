package com.wink.dbse.command.game

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.entity.BangEntity
import com.wink.dbse.entity.game.BangCache
import com.wink.dbse.entity.game.BangResult
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

private var chambers = 6

@Component
class Bang(
    private val bangCache: BangCache
) : Command() {

    private val random = Random()

    init {
        name = "bang"
        help = "Play a game of Russian Roulette"
    }

    override fun execute(event: CommandEvent) {
        val numChambers = chambers
        val result: BangResult = pullTrigger()
        val bangEntity = BangEntity(null, event.author.idLong, LocalDateTime.now(ZoneOffset.UTC), result.value, numChambers)
        bangCache.add(bangEntity)
    }

    private fun pullTrigger(): BangResult {
        val pull = random.nextInt(chambers)
        return if (pull == 0) {
            if (chambers == 1) {
                chambers = 6
                if (random.nextInt(10) == 0) BangResult.JAM else BangResult.DIE
            } else {
                chambers = 6
                BangResult.DIE
            }
        } else {
            chambers--
            BangResult.LIVE
        }
    }
}
