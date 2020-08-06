package com.wink.kotlinbot.entity.game.cardgame.game

import com.wink.kotlinbot.entity.game.cardgame.CardGame
import com.wink.kotlinbot.entity.game.cardgame.CardGameCpuPlayer
import com.wink.kotlinbot.entity.game.cardgame.CardGameUserPlayer
import com.wink.kotlinbot.entity.game.cardgame.ICardGamePlayer
import com.wink.kotlinbot.entity.game.cardgame.card.CardRank
import com.wink.kotlinbot.entity.game.cardgame.card.Deck
import com.wink.kotlinbot.entity.game.cardgame.card.Hand

class BlackJack(vararg players: CardGameUserPlayer) : CardGame(Deck.create(5), *players) {

    enum class Move {
        STAND, HIT, DOUBLE
    }

    enum class WinReason {
        BLACK_JACK, TWENTY_ONE, HIGH_HAND, NO_BUST, NO_WINNER
    }

    private val dealer = CardGameCpuPlayer()
    override val winners = mutableListOf<ICardGamePlayer>()
    private var winReason: WinReason = WinReason.NO_WINNER

    override fun start() {
        isOver = false
        winners.clear()
        deck.shuffle()
        dealer.hand.addAll(deck.drawFromTop(2))
        players.forEach {
            it.isDone = false
            it.hand.clear()
            it.hand.add(deck.pickTopCard())
        }
    }

    fun makeMove(player: ICardGamePlayer, move: Move) {
        if (player != dealer && player !in players) {
            throw IllegalArgumentException("Player must be in this game to make a move")
        }
        when (move) {
            Move.STAND -> stand(player)
            Move.HIT -> hit(player)
            Move.DOUBLE -> double(player)
        }
    }

    private fun stand(player: ICardGamePlayer) {
        player.isDone = true
        if (players.all { it.isDone }) {
            dealerPlays()
        }
    }

    private fun hit(player: ICardGamePlayer) {
        player.hand.add(deck.pickTopCard())
    }

    private fun double(player: ICardGamePlayer) {
        // TODO
    }

    private fun dealerPlays() {
        while (valueOf(dealer.hand) < 17) {
            hit(dealer)
        }
        dealer.isDone = true
        decideWinner()
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