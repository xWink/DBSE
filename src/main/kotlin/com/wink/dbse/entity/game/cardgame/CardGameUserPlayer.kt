package com.wink.dbse.entity.game.cardgame

import com.wink.dbse.entity.game.UserPlayer
import com.wink.dbse.entity.game.cardgame.card.Hand
import net.dv8tion.jda.api.entities.User

open class CardGameUserPlayer(
        user: User,
        override val hand: Hand = Hand()
) : UserPlayer(user), ICardGamePlayer