package com.wink.dbse.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "bot.channel.name")
class ChannelIds(
        var welcome: String? = null,
        var deletedMessages: String? = null,
        var bulkDeletedMessages: String? = null,
        var editedMessages: String? = null,
        var channelOptions: String? = null
)