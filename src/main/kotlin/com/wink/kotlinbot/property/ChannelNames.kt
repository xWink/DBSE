package com.wink.kotlinbot.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "channel.name")
data class ChannelNames(
        var welcome: String? = null,
        var deletedMessages: String? = null,
        var bulkDeletedMessages: String? = null,
        var editedMessages: String? = null,
        var channels: String? = null
)