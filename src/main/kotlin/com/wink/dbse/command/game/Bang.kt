package com.wink.dbse.command.game

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.repository.BangRepository
import com.wink.dbse.service.Messenger
import org.springframework.stereotype.Component
import java.util.*

@Component
class Bang(
        private val messenger: Messenger,
        private val bangCache: BangCache
) : Command() {

    override fun execute(event: CommandEvent) {

    }
}

@Component
class BangCache(private val bangRepository: BangRepository) {

    private val updates = HashMap<Long, BangUpdate>()
    private var panic = false

    fun addBang(update: BangUpdate) {
        val existingUpdate = updates[update.userId]
        if (existingUpdate == null) {
            updates[update.userId] = update
        } else {
            existingUpdate.attempts += update.attempts
            existingUpdate.deaths += update.deaths
            existingUpdate.jams += update.jams
        }
    }
}

class BangUpdate(
        val userId: Long,
        var attempts: Int,
        var deaths: Int = 0,
        var jams: Int = 0
) {

}
