package com.wink.kotlinbot.entity.game

abstract class Game {

    protected var isOver: Boolean = true
    protected abstract val winners: MutableList<out IPlayer>

    abstract fun start()
}