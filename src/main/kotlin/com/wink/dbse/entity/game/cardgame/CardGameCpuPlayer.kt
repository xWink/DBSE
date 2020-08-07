package com.wink.dbse.entity.game.cardgame

import com.wink.dbse.entity.game.CpuPlayer
import com.wink.dbse.entity.game.cardgame.card.Hand

open class CardGameCpuPlayer(
        override val hand: Hand = Hand()
) : CpuPlayer(), ICardGamePlayer