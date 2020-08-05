package com.wink.kotlinbot.entity.game.cardgame

import com.wink.kotlinbot.entity.game.UserPlayer
import com.wink.kotlinbot.entity.game.cardgame.card.Hand
import net.dv8tion.jda.api.entities.User

open class CardGameUserPlayer(
        user: User,
        override val hand: Hand = Hand()
) : UserPlayer(user), ICardGamePlayer