package com.wink.dbse.entity.game.cardgame

import com.wink.dbse.entity.game.Game
import com.wink.dbse.entity.game.cardgame.card.Deck

abstract class CardGame(
        override val player: CardGameUserPlayer,
        val deck: Deck = Deck.create()) : Game(player)