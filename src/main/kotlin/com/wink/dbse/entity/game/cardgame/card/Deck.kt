package com.wink.dbse.entity.game.cardgame.card

class Deck private constructor(private val cards: MutableList<Card>) {

    fun shuffle() = cards.shuffle()

    fun size(): Int = cards.size

    fun insertCardAt(index: Int, card: Card) = cards.add(index, card)

    fun showCardAt(index: Int): Card = cards[index]

    fun pickTopCard(): Card = cards.removeAt(0)

    fun pickBottomCard(): Card = cards.removeAt(cards.lastIndex)

    fun pickRandomCard(): Card = cards.removeAt(Math.random().times(cards.size).toInt())

    fun drawFromTop(numCards: Int = 1): ArrayList<Card> {
        val drawn = ArrayList<Card>()
        for (i in 1..numCards) {
            drawn.add(cards.removeAt(0))
        }
        return drawn
    }

    fun drawFromBottom(numCards: Int = 1): ArrayList<Card> {
        val drawn = ArrayList<Card>()
        for (i in 1..numCards) {
            drawn.add(cards.removeAt(cards.lastIndex))
        }
        return drawn
    }

    fun drawRandom(numCards: Int = 1): ArrayList<Card> {
        val drawn = ArrayList<Card>()
        for (i in 1..numCards) {
            drawn.add(cards.removeAt(Math.random().times(cards.size).toInt()))
        }
        return drawn
    }

    companion object {
        fun create(numSets: Int = 1): Deck {
            val deck = Deck(ArrayList())
            for (i in 1..numSets) {
                deck.cards.addAll(Card.set())
            }
            return deck
        }

        fun createEmpty() = Deck(ArrayList())

        fun of(cards: Collection<Card>) = Deck(cards.toMutableList())
    }
}