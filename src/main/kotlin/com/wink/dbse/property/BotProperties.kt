package com.wink.dbse.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "bot")
class BotProperties(
        var token: String? = null,
        var serverId: String? = null,
        var ownerId: String? = null,
        var commandPrefix: String? = null
)