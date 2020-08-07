package com.wink.dbse.entity.game

abstract class Game(open val player: UserPlayer) {

    protected var isOver: Boolean = true

    abstract fun start()
}