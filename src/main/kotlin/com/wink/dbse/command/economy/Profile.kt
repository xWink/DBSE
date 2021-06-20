package com.wink.dbse.command.economy

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.command.game.BangResult
import com.wink.dbse.entity.BangEntity
import com.wink.dbse.entity.UserEntity
import com.wink.dbse.extension.safeName
import com.wink.dbse.repository.BangRepository
import com.wink.dbse.repository.UserRepository
import com.wink.dbse.service.Messenger
import net.dv8tion.jda.api.EmbedBuilder
import org.springframework.stereotype.Component
import java.awt.Color
import kotlin.math.roundToInt

@Component
class Profile(
    private val messenger: Messenger,
    private val userRepository: UserRepository,
    private val bangRepository: BangRepository
    ) : Command() {

    init {
        name = "profile"
        help = "todo"
    }

    private val eb = EmbedBuilder()

    override fun execute(event: CommandEvent) {
        eb.clear()
        setHeading(event)
        generateBangInfo(bangRepository.findBangEntitiesByUserId(event.author.idLong))
        setUserInfo(userRepository.findById(event.author.idLong).get())
        messenger.sendMessage(event.channel, eb.build())
    }

    private fun setUserInfo(user: UserEntity) {
        eb.addField("Streak", "${user.bangStreak}" + if(user.bangStreak > 0)" :fire:" else "", true)
        eb.addField("Karma","${user.upVotes - user.downVotes}",true)
        eb.addField("Wallet","${user.wallet} gc",true)
    }

    private fun generateBangInfo(bangs: List<BangEntity>) {
        var jams = 0
        var deaths = 0

        for (bang in  bangs) {
            when (bang.result) {
                BangResult.DIE.value-> deaths++
                BangResult.JAM.value-> jams++
            }
        }
        setBangInfo(bangs.size, deaths, jams)
    }


    private fun setBangInfo (bangs: Int, deaths: Int, jams: Int) {
        eb.addField("Bangs", "$bangs",true)
        eb.addField("Deaths", "$deaths",true)
        eb.addField("Jams", "$jams",true)
        eb.addField("Survival Rate",  "${getSurvivalRate(deaths.toFloat(), bangs)}%",true)
    }

    private fun getSurvivalRate (deaths: Float, bangs: Int): Double {
        return if(bangs > 0) ((deaths.toDouble().div(bangs) * 100 * 100).roundToInt() / 100.00) else 100.00
    }

    private fun setHeading (event: CommandEvent) {
        eb.setTitle( "${event.author.safeName()}'s Profile")
        eb.setThumbnail(event.author.avatarUrl)
        eb.setColor(if(event.member != null) event.member.color else Color.LIGHT_GRAY)
    }






}