package com.wink.dbse.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("bot.role.id")
class RoleIds(
        var acceptedToS: String? = null,
        var globalAccess: Array<String>? = null,
        var notify: String? = null
)