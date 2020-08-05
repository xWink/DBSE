package com.wink.kotlinbot.entity.game

abstract class Game {

    protected var active: Boolean = false

    abstract fun start()
}