package com.wink.dbse.entity.game

abstract class Game(open val player: UserPlayer) {

    var isOver: Boolean = true
        protected set

    abstract fun start()
}