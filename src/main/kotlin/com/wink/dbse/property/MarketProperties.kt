package com.wink.dbse.property

import com.wink.dbse.entity.economy.RoleListing
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "bot.market")
class MarketProperties(var listings: List<RoleListing>? = null)
