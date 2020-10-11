package com.wink.dbse.entity.economy

import com.wink.dbse.property.MarketProperties
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
class Marketplace @Lazy constructor(marketProperties: MarketProperties) {

    val listings: ArrayList<RoleListing> = ArrayList()

    init {
        marketProperties.listings?.forEach { listings.add(it) }
    }

    override fun toString(): String {
        var id = 1
        return listings.joinToString("\n") { "${id++}. ${it.name}: ${it.cost} gc" }
    }
}
