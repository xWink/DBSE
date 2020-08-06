package com.wink.dbse.entity.game.cardgame.card

class Hand : ArrayList<Card>() {

    override fun toString(): String = joinToString(" ")
}