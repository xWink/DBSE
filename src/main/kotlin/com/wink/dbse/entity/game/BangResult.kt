package com.wink.dbse.entity.game

enum class BangResult(val value: Int) {
    DIE(-1),
    LIVE(0),
    JAM(1);

    companion object {
        fun of(num: Int): BangResult {
            return when(num) {
                -1 -> DIE
                0 -> LIVE
                1 -> JAM
                else -> throw IllegalArgumentException()
            }
        }
    }
}