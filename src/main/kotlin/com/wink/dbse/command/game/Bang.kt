/*
 * If you are reading this, I know it's awful. I just needed something that works, please don't hate me :(
 */

package com.wink.dbse.command.game

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.entity.BangEntity
import com.wink.dbse.property.ChannelIds
import com.wink.dbse.property.EmoteIds
import com.wink.dbse.repository.BangRepository
import com.wink.dbse.service.Messenger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
// TODO: Make sure to escape _ - ~ chars in people's names

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
        val result: BangResult = pullTrigger()
        val bangEntity = BangEntity(null, event.author.idLong, LocalDateTime.now(ZoneOffset.UTC), result.value)
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

enum class BangResult(val value: Int) {
    DIE(-1),
    LIVE(0),
    JAM(1);

    companion object {
        fun of(num: Int): BangResult {
            return when(num) {
                -1 -> DIE
                0 -> LIVE
                1 -> JAM
                else -> throw IllegalArgumentException()
            }
        }
    }
}

@Component
class BangCache(
        channelIds: ChannelIds,
        emoteIds: EmoteIds,
        private val messenger: Messenger,
        private val bangRepository: BangRepository,
        private val jda: JDA
) {

    private val excitementEmote: Emote? = jda.getEmoteById(emoteIds.excitement ?: "")
    private val bangChannel: TextChannel? = jda.getTextChannelById(channelIds.spamChannel ?: "")
    private val threshold: Long = 12000
    private var timer: Timer? = null
    private val queue = LinkedList<BangEntity>()
    private val last20 = LinkedList<BangEntity>()
    final var panic = false
        private set

    fun add(bangEntity: BangEntity) {
        queue.add(bangEntity)
        last20.add(bangEntity)
        if (last20.size > 20) {
            last20.remove(last20.first())
        }

        checkPanic()
        if (!panic) {
            printBangResult(bangEntity)
        } else {
            queue.add(bangEntity)
        }
    }

    private fun printBangResult(bangEntity: BangEntity) {
        val name = jda.getUserById(bangEntity.userId!!)?.name ?: "Unknown user"
        val excite = excitementEmote?.asMention ?: ":O"
        var output: String = when(BangResult.of(bangEntity.result!!)) {
            BangResult.LIVE -> "Click. $name survived! $excite"
            BangResult.DIE -> "Bang! $name died :skull:"
            BangResult.JAM -> "Click. The gun jammed... $name survived $excite $excite $excite"
        }
        output += "\nChambers left in cylinder: ||  $chambers  ||"
        messenger.sendMessage(bangChannel ?: return, output)
    }

    private fun flush() {
        for (bang in queue) {
            bangRepository.save(bang)
        }
        queue.clear()
    }

    private fun checkPanic() {
        val avgTime: Long = last20
                .mapNotNull { it.timeOccurred?.toEpochSecond(ZoneOffset.UTC)?.times(1000) }
                .reduce { a, b -> a + b }
                .div(last20.size)

        println("Average time = $avgTime ms ago")
        println("Threshold = ${Date().time - threshold} ms ago")
        println("Size = ${last20.size}")

        if (avgTime > Date().time - threshold && last20.size >= 20) {
            panic = true
        }

        when {
            timer != null -> {
                timer!!.cancel()
                initTimer()
            }
            panic -> initTimer()
            else -> flush()
        }
    }

    fun printResults() {
        if (queue.size == 0) {
            return
        }

        val updates = HashMap<Long, LinkedList<BangEntity>>()
        for (bang in queue) {
            updates.getOrDefault(bang.userId, LinkedList()).add(bang)
        }

        var output = "**Combined Data:**\n"
        for (userUpdates in updates.values) {
            val user = jda.getUserById(userUpdates[0].userId ?: continue) ?: continue
            output += """
                **${user.name}:**
                Attempts: ${userUpdates.size}
                Deaths: ${userUpdates.count { it.result == BangResult.DIE.value }}
                Jams: ${userUpdates.count { it.result == BangResult.JAM.value }}
            """.trimIndent()
        }

        messenger.sendMessage(bangChannel ?: return, output)
    }

    private val task = object : TimerTask() {
        override fun run() {
            timer?.cancel()
            timer = null
            printResults()
            flush()
            panic = false
        }
    }

    private fun initTimer() {
        timer = Timer()
        timer!!.schedule(task, 1000 * 10)
    }
}
