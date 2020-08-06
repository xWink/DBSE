package com.wink.dbse.entity.game.cardgame.card

import java.util.HashMap

enum class CardRank(val symbol: String, val value: Int) {

    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    JACK("J", 10),
    QUEEN("Q", 10),
    KING("K", 10),
    ACE("A", 11);

    companion object {

        private val MY_MAP: MutableMap<String, CardRank> = HashMap()

        fun of(symbol: String?): CardRank? {
            return MY_MAP[symbol]
        }

        init {
            for (cardRank in values()) {
                MY_MAP[cardRank.symbol] = cardRank
            }
        }
    }
}