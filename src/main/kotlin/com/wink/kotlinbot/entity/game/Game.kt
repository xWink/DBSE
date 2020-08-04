package com.wink.kotlinbot.entity.game

abstract class Game(open val players: List<Player> = ArrayList()) {

    abstract fun start()
}