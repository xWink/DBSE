package com.wink.kotlinbot.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "bot")
data class BotProperties(
        var token: String? = null,
        var ownerId: String? = null,
        var commandPrefix: String? = null
)