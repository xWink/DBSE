package com.wink.dbse.entity.game.cardgame

import com.wink.dbse.entity.game.IPlayer
import com.wink.dbse.entity.game.cardgame.card.Hand

interface ICardGamePlayer : IPlayer {

    val hand: Hand
    var isDone: Boolean
}