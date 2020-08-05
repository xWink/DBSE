package com.wink.kotlinbot.entity.game.cardgame

import com.wink.kotlinbot.entity.game.CpuPlayer
import com.wink.kotlinbot.entity.game.cardgame.card.Hand

open class CardGameCpuPlayer(override val hand: Hand = Hand()) : CpuPlayer(), ICardGamePlayer