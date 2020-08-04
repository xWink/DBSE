package com.wink.kotlinbot.entity.game.cardgame

import com.wink.kotlinbot.entity.game.Player
import com.wink.kotlinbot.entity.game.cardgame.card.Hand
import net.dv8tion.jda.api.entities.User

open class CardGamePlayer(
        user: User,
        val hand: Hand = Hand()
) : Player(user) {


}