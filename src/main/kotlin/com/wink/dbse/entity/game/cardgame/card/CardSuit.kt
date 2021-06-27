package com.wink.dbse.entity.game.cardgame.card

enum class CardSuit(val symbol: String, val initial: String) {

    SPADES("♠", "s"),
    CLUBS("♣", "c"),
    HEARTS("♥", "h"),
    DIAMONDS("♦", "d");

    companion object {

        private val MY_MAP: MutableMap<String, CardSuit> = HashMap()

        fun of(initial: String?): CardSuit? {
            return MY_MAP[initial]
        }

        init {
            for (cardSuit in values()) {
                MY_MAP[cardSuit.initial] = cardSuit
            }
        }
    }
}
