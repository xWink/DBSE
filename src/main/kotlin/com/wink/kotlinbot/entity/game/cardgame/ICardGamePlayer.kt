package com.wink.kotlinbot.entity.game.cardgame

import com.wink.kotlinbot.entity.game.IPlayer
import com.wink.kotlinbot.entity.game.cardgame.card.Hand

interface ICardGamePlayer : IPlayer {

    val hand: Hand
    var isDone: Boolean
}