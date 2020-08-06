package com.wink.kotlinbot.entity.game.cardgame

import com.wink.kotlinbot.entity.game.Game
import com.wink.kotlinbot.entity.game.IPlayer
import com.wink.kotlinbot.entity.game.cardgame.card.Deck

abstract class CardGame(
        val deck: Deck = Deck.create(),
        vararg val players: CardGameUserPlayer
) : Game() {
    abstract override val winners: MutableList<out ICardGamePlayer>
}