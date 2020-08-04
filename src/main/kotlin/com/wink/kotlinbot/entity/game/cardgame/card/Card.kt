package com.wink.kotlinbot.entity.game.cardgame.card

class Card(val rank: CardRank, val suit: CardSuit) {

    override fun toString(): String = "[" + rank.symbol + " " + suit.symbol + "]"

    companion object {
        fun set(): Set<Card> {
            val cards = HashSet<Card>()
            for (suit in CardSuit.values()) {
                for (rank in CardRank.values()) {
                    cards.add(Card(rank, suit))
                }
            }
            return cards
        }
    }
}