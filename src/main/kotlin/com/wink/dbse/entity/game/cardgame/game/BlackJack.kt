package com.wink.dbse.entity.game.cardgame.game

import com.wink.dbse.entity.game.cardgame.CardGame
import com.wink.dbse.entity.game.cardgame.CardGameCpuPlayer
import com.wink.dbse.entity.game.cardgame.CardGameUserPlayer
import com.wink.dbse.entity.game.cardgame.ICardGamePlayer
import com.wink.dbse.entity.game.cardgame.card.CardRank
import com.wink.dbse.entity.game.cardgame.card.Deck
import com.wink.dbse.entity.game.cardgame.card.Hand

class BlackJack(player: CardGameUserPlayer) : CardGame(player, Deck.create(5)) {

    private val dealer = CardGameCpuPlayer()

    override fun start() {
        isOver = false
        deck.shuffle()
        dealer.hand.addAll(deck.drawFromTop(2))
        player.hand.clear()
        player.hand.addAll(deck.drawFromTop(2))
    }

    enum class Move {
        STAND, HIT, DOUBLE
    }

    fun makeMove(move: Move) {
        when (move) {
            Move.STAND -> stand()
            Move.HIT -> hit()
            Move.DOUBLE -> double()
        }
    }

    private fun stand() {
        dealerPlays()
    }

    private fun hit() {
        player.hand.add(deck.pickTopCard())
    }

    private fun double() {
        // TODO
    }

    private fun dealerPlays() {
        while (valueOf(dealer.hand) < 17) {
            dealer.hand.add(deck.pickTopCard())
        }
        decideWinner()
    }

    enum class WinReason {
        BLACK_JACK, TWENTY_ONE, HIGH_HAND, NO_BUST, NO_WINNER
        //TODO: ADD CALCULATION?
    }

    private fun decideWinner() {
        // TODO
        isOver = true
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