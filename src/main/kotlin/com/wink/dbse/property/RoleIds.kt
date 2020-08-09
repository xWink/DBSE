package com.wink.dbse.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("bot.role.id")
class RoleIds(
        var globalAccess: Array<String>? = null,
        var muted: String? = null,
        var notify: String? = null
)