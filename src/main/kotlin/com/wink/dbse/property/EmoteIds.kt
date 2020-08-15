package com.wink.dbse.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "bot.emote.id")
class EmoteIds(
        var confirm: String? = null,
        var upVote: String? = null,
        var downVote: String? = null
)