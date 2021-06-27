package com.wink.dbse.entity.game

import com.wink.dbse.entity.BangEntity
import com.wink.dbse.entity.TodayBangerEntity
import com.wink.dbse.entity.YesterdayBangerEntity
import com.wink.dbse.extension.safeName
import com.wink.dbse.property.ChannelIds
import com.wink.dbse.property.EmoteIds
import com.wink.dbse.repository.*
import com.wink.dbse.service.Messenger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.ZoneOffset
import java.util.*

@Component
class BangCache(
    channelIds: ChannelIds,
    emoteIds: EmoteIds,
    private val messenger: Messenger,
    private val bangRepository: BangRepository,
    private val todayBangerRepository: TodayBangerRepository,
    private val yesterdayBangerRepository: YesterdayBangerRepository,
    private val userRepository: UserRepository,
    private val jda: JDA
) {
    private val excitementEmote: Emote? = jda.getEmoteById(emoteIds.excitement ?: "")
    private val bangChannel: TextChannel = jda.getTextChannelById(channelIds.spamChannel
        ?: throw IllegalStateException("bangChannel not specified"))!!
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
            performBangForResult(bangEntity)
        }
    }

    private fun performBangForResult (bangEntity: BangEntity) {
        val money = performDailyUpdate(bangEntity.userId!!) +
                if (bangEntity.result ==  BangResult.JAM.value) 50 else 0
        userRepository.addMoney(bangEntity.userId, money)
    }

    private fun performDailyUpdate(userId: Long): Long {
        if (todayBangerRepository.userNotBangedToday(userId)) {
            todayBangerRepository.save(TodayBangerEntity(userId))
            userRepository.incrementBangStreak(userId)
            return 5L + if ( userRepository.findById(userId).get().bangStreak % 10 == 0)  50L else 0L
        }
        return 0L
    }

    private fun printBangResult(bangEntity: BangEntity) {
        val name = jda.getUserById(bangEntity.userId!!)?.safeName() ?: "Unknown user"
        val output = getShotResultString(name, bangEntity.result!!, bangEntity.chambers!!) + getDailyResultOutputString(name, bangEntity.userId) +
                getJamOutputString(name, bangEntity)
        messenger.sendMessage(bangChannel, output)
    }

    private fun getShotResultString (name: String, result: Int, chambers: Int ): String {
        val excite = excitementEmote?.asMention ?: ":O"
        val output: String = when (BangResult.of(result)) {
            BangResult.LIVE -> "Click. $name survived! $excite"
            BangResult.DIE -> "Bang! $name died :skull:"
            BangResult.JAM -> "Click. The gun jammed... $name survived $excite $excite $excite"
        }
        return "$output\nChambers left in cylinder: ||  " +
                "${ if(BangResult.of(result) == BangResult.LIVE) chambers - 1 else 6 }  ||"
    }

    private fun getJamOutputString(name: String, bangEntity: BangEntity): String {
        return if (bangEntity.result == BangResult.JAM.value) "\n$name received a bonus 50 GryphCoins for Jamming!"
        else ""
    }

    private fun getDailyResultOutputString (name: String, userId: Long): String {
        return if (todayBangerRepository.userNotBangedToday(userId))
            "\n$name received their daily reward of 5 GryphCoins!\n" + getBonusForStreakString(userId) else "\n"
    }

    private fun getBonusForStreakString (userId: Long): String {
        return if ((userRepository.findById(userId).get().bangStreak + 1) % 10 == 0 )
            "A bonus 50 GryphCoins were awarded for the current streak\n" else ""
    }

    private fun checkPanic() {
        val avgTime: Long = last20
            .mapNotNull { it.timeOccurred?.toEpochSecond(ZoneOffset.UTC)?.times(1000) }
            .reduce { a, b -> a + b }
            .div(last20.size)

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
        manageQueue()
        panic = false
    }

    private fun manageQueue () {
        if (queue.isEmpty()) {
            return
        }
        val updates = HashMap<Long, LinkedList<BangEntity>>()
        queue.forEach { updates.getOrPut(it.userId!!) { LinkedList() }.add(it) }

        printResults(updates)
        performResultUpdates(updates)
        flush()
    }

    private fun printResults(updates: HashMap<Long, LinkedList<BangEntity>>) {
        messenger.sendMessage(bangChannel, compilePanicAttemptsOutput(updates))
    }

    private fun compilePanicAttemptsOutput(updates: HashMap<Long, LinkedList<BangEntity>>): String {
        var output = "**Combined Data:**\n"
        for (userUpdates in updates.values) {
            val user = jda.getUserById(userUpdates[0].userId ?: continue) ?: continue
            output += compiledIndividualPanicOutputString(userUpdates, user)
        }
        return output
    }

    private fun compiledIndividualPanicOutputString(userUpdates: LinkedList<BangEntity>, user: User): String {
        var output = """
                **${user.safeName()}:**
                Attempts: ${userUpdates.size}
                Deaths: ${userUpdates.count { it.result == BangResult.DIE.value }}
                Jams: ${userUpdates.count { it.result == BangResult.JAM.value }}
            """.trimIndent()
        output += getDailyResultOutputString(user.safeName(), user.idLong)
        return output
    }

    private fun performResultUpdates(updates: HashMap<Long, LinkedList<BangEntity>>) {
        for (userUpdates in updates.values) {
            performIndividualResultUpdate(userUpdates, userUpdates[0].userId ?: continue)
        }
    }

    private fun performIndividualResultUpdate(userUpdates: LinkedList<BangEntity>, userId: Long) {
        val money = userUpdates.count { it.result == BangResult.JAM.value } * 50L + performDailyUpdate(userId)
        userRepository.addMoney(userId, money)
    }

    private fun flush() {
        queue.forEach{ bangRepository.save(it)}
        queue.clear()
    }

    @Scheduled(cron = "59 59 23 * * ?", zone = "GMT-4:00")
    private fun resetTables () {
        logger.info("Updating the Bang streak for the day")

        if(panic && timer != null) {
            reset = true
            finishTimer()
            reset = false
            initTimer()
        }

        val yesterday = yesterdayBangerRepository.findAll()
        val today = todayBangerRepository.findAll().mapNotNull { YesterdayBangerEntity(it.userId) }
        clearDailyTables()
        updateUserStreaks(yesterday, today)
        logger.info("Updated the Yesterday table with Today's table")
    }

    private fun updateUserStreaks(yesterday: MutableList<YesterdayBangerEntity>, today: List<YesterdayBangerEntity>) {
        yesterday.removeAll(today)
        yesterday.forEach {userRepository.clearBangStreak(it.userId)}
        yesterdayBangerRepository.saveAll(today)
    }

    private fun clearDailyTables() {
        yesterdayBangerRepository.deleteAll()
        todayBangerRepository.deleteAll()
    }


    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(BangCache::class.java)
    }

}