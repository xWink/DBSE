package com.wink.kotlinbot.entity.game.cardgame

import com.wink.kotlinbot.entity.game.cardgame.card.CardRank
import com.wink.kotlinbot.entity.game.cardgame.card.Deck
import com.wink.kotlinbot.entity.game.cardgame.card.Hand

class BlackJack(override val players: List<CardGamePlayer>) : CardGame(players, Deck.create(5)) {

    private val dealerHand = Hand()

    override fun start() {
        deck.shuffle()
        dealerHand.addAll(deck.drawFromTop(2))
        players.forEach {
            it.hand.clear()
            it.hand.addAll(deck.drawFromTop(2))
        }
    }

    fun hit(player: CardGamePlayer): Int {
        player.hand.add(deck.pickTopCard())
        return valueOf(player.hand)
    }

    fun valueOf(hand: Hand): Int {
        var value = 0
        var numAces = 0
        for (card in hand) {
            if (card.rank == CardRank.ACE) {
                numAces++
            }
            value += card.rank.value
        }
        while (numAces > 0 && value > 21) {
            numAces--
            value -= 10
        }
        return value
    }
}