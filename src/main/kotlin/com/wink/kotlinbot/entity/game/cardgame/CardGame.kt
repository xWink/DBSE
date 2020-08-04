package com.wink.kotlinbot.entity.game.cardgame

import com.wink.kotlinbot.entity.game.Game
import com.wink.kotlinbot.entity.game.cardgame.card.Deck

abstract class CardGame(
        players: List<CardGamePlayer> = ArrayList(),
        val deck: Deck = Deck.create()
) : Game(players)