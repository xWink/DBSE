package com.wink.kotlinbot.entity.game.cardgame.game

import com.wink.kotlinbot.entity.game.cardgame.CardGame
import com.wink.kotlinbot.entity.game.cardgame.CardGameCpuPlayer
import com.wink.kotlinbot.entity.game.cardgame.CardGameUserPlayer
import com.wink.kotlinbot.entity.game.cardgame.ICardGamePlayer
import com.wink.kotlinbot.entity.game.cardgame.card.CardRank
import com.wink.kotlinbot.entity.game.cardgame.card.Deck
import com.wink.kotlinbot.entity.game.cardgame.card.Hand

class BlackJack(vararg players: CardGameUserPlayer) : CardGame(Deck.create(5), *players) {

    private val dealer = CardGameCpuPlayer()

    override fun start() {
        active = true
        deck.shuffle()
        dealer.hand.addAll(deck.drawFromTop(2))
        players.forEach {
            it.hand.clear()
            it.hand.add(deck.pickTopCard())
        }
    }

    fun hit(player: ICardGamePlayer) {
        if (player == dealer || player in players) {
            player.hand.add(deck.pickTopCard())
        } else {
            throw IllegalArgumentException("Player must be in this game to hit")
        }
    }

    fun dealerPlays() {
        while (valueOf(dealer.hand) < 17) {
            hit(dealer)
        }
    }

    companion object {
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
}