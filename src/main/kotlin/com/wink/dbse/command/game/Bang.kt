/*
 * If you are reading this, I know it's awful. I just needed something that works, please don't hate me :(
 */

package com.wink.dbse.command.game

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.entity.BangEntity
import com.wink.dbse.entity.TodayBangerEntity
import com.wink.dbse.entity.YesterdayBangerEntity
import com.wink.dbse.extension.safeName
import com.wink.dbse.property.ChannelIds
import com.wink.dbse.property.EmoteIds
import com.wink.dbse.repository.BangRepository
import com.wink.dbse.repository.YesterdayBangerRepository
import com.wink.dbse.repository.TodayBangerRepository
import com.wink.dbse.repository.UserRepository
import com.wink.dbse.service.Messenger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
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
    private val channelIds: ChannelIds,
    private val emoteIds: EmoteIds,
    private val messenger: Messenger,
    private val bangRepository: BangRepository,
    private val todayBangerRepository: TodayBangerRepository,
    private val yesterdayBangerRepository: YesterdayBangerRepository,
    private val userRepository: UserRepository,
    private val jda: JDA) {

    private val excitementEmote: Emote? = jda.getEmoteById(emoteIds.excitement ?: "")
    private val bangChannel: TextChannel? = jda.getTextChannelById(channelIds.spamChannel ?: "")
    private val threshold: Long = 12000
    private var timer: Timer? = null
    private val queue = LinkedList<BangEntity>()
    private val last20 = LinkedList<BangEntity>()
    final var panic = false
        private set
    final var reset = false
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
        }
    }

    private fun printBangResult(bangEntity: BangEntity) {
        val name = jda.getUserById(bangEntity.userId!!)?.safeName()?: "Unknown user"
        val output = printShotResult(name, bangEntity.result!!) + printDailyResult(name, bangEntity.userId) +
                printJamResult(name, bangEntity)
        messenger.sendMessage(bangChannel ?: return, output)
    }

    private fun printJamResult(name: String, bangEntity: BangEntity): String {
        var output = ""
        if(bangEntity.result == BangResult.JAM.value) {
            output += "\n$name received a bonus 50 GryphCoins for Jamming!"
            userRepository.addMoney(bangEntity.userId!!, 50L)
        }
        return output
    }

    private fun printShotResult (name: String, result: Int ): String {
        val excite = excitementEmote?.asMention ?: ":O"
        val output: String = when(BangResult.of(result)) {
            BangResult.LIVE -> "Click. $name survived! $excite"
            BangResult.DIE -> "Bang! $name died :skull:"
            BangResult.JAM -> "Click. The gun jammed... $name survived $excite $excite $excite"
        }
        return "$output\nChambers left in cylinder: ||  $chambers  ||"
    }

    private fun printDailyResult (name: String, userId: Long): String {
        return if (todayBangerRepository.findFirstByUserId(userId) == null)
             "\n$name received their daily reward of 5 GryphCoins!" +
                    if(addDaily(userId) > 5)  "\nA bonus 50 GryphCoins were awarded for the current streak" else ""
                        else ""
    }

    private fun daily(): LinkedList<Pair<Long, Long>> {
        val list = LinkedList<Pair<Long, Long>>()
        queue.distinctBy { it.userId }.filter { todayBangerRepository.findFirstByUserId(it.userId!!) == null }.forEach {
            list.add(Pair(it.userId!!, addDaily(it.userId)))
        }
        return list
    }

    private fun flush() {
        queue.forEach{ bangRepository.save(it)}
        queue.clear()
    }

    private fun addDaily(userId: Long) : Long {
        todayBangerRepository.save(TodayBangerEntity(userId))
        userRepository.incrementBangStreak(userId)
        val money =  5L + if (userRepository.findById(userId).get().bangStreak % 10 == 0)  50L else 0L
        userRepository.addMoney(userId, money)
        return money
    }

    private fun checkPanic() {
        val avgTime: Long = last20
                .mapNotNull { it.timeOccurred?.toEpochSecond(ZoneOffset.UTC)?.times(1000) }
                .reduce { a, b -> a + b }.div(last20.size)

        panic = (avgTime > Date().time - threshold && last20.size >= 20)  || timer != null || reset

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
        if (queue.isEmpty()) {
            return
        }
        val updates = HashMap<Long, LinkedList<BangEntity>>()
        queue.forEach {   updates.getOrPut(it.userId!!) { LinkedList() }.add(it) }

        messenger.sendMessage(bangChannel ?: return, compileAttemptsOutput(updates))
    }

    private fun compileAttemptsOutput(updates: HashMap<Long, LinkedList<BangEntity>>): String {
        var output = "**Combined Data:**\n"
        for (userUpdates in updates.values) {
            output += compiledIndividual(userUpdates,jda.getUserById(userUpdates[0].userId ?: continue)
                ?: continue)
        }
        return output + dailyUpdates()
    }

    private fun compiledIndividual(userUpdates: LinkedList<BangEntity>, user: User): String {
        val jams = userUpdates.count { it.result == BangResult.JAM.value }
        userRepository.addMoney(user.idLong,jams * 50L)
        return """
                **${user.safeName()}:**
                Attempts: ${userUpdates.size}
                Deaths: ${userUpdates.count { it.result == BangResult.DIE.value }}
                Jams: $jams
                
            """.trimIndent()
    }

    private fun dailyUpdates () :String {
        val userUpdateList = daily()
        var output = if(userUpdateList.isNotEmpty()) { "**Daily Updates**\n" } else ""
        for (userStreak in userUpdateList ) {
            val user = jda.getUserById(userStreak.first) ?: continue
            output += """
                **${user.safeName()}** received their daily reward of ${userStreak.second} GryphCoins!
                
            """.trimIndent()
        }
        return output
    }


    private fun initTimer() {
        timer = Timer()
        timer!!.schedule(initTask(), 1000 * 10, 1000)
    }

    private fun initTask(): TimerTask {
        return object : TimerTask() {
            //Kills the current timer task and flushes the queue
            override fun run() {
                if (!reset) finishTimer()
            }
        }
    }

    private fun finishTimer () {
        timer?.cancel()
        timer = null
        printResults()
        flush()
        panic = false
    }

    @Scheduled(cron = "59 59 23 * * ?", zone = "GMT-4:00")
    private fun resetTables () {
        logger.info("Updating the Bang streak for the day")

        if(panic  && timer != null) {
            reset = true
            finishTimer()
            reset = false
            initTimer()
        }

        val yesterday = yesterdayBangerRepository.findAll()
        val today = todayBangerRepository.findAll().mapNotNull { YesterdayBangerEntity(it.userId) }
        clearDailyTables()
        updateUserStreaks(yesterday, today)
        reset = false
        logger.info("Completed Migrating the tables")
    }

    private fun clearDailyTables() {
        yesterdayBangerRepository.deleteAll()
        todayBangerRepository.deleteAll()
    }

    private fun updateUserStreaks(yesterday: MutableList<YesterdayBangerEntity>, today: List<YesterdayBangerEntity>) {
        yesterday.removeAll(today)
        yesterday.forEach {userRepository.clearBangStreak(it.userId)}
        yesterdayBangerRepository.saveAll(today)
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(BangCache::class.java)
    }

}



