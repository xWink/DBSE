package com.wink.dbse.entity.game.cardgame

import com.wink.dbse.entity.game.Game
import com.wink.dbse.entity.game.cardgame.card.Deck

abstract class CardGame(
        val deck: Deck = Deck.create(),
        vararg val players: CardGameUserPlayer
) : Game() {
    abstract override val winners: MutableList<out ICardGamePlayer>
}