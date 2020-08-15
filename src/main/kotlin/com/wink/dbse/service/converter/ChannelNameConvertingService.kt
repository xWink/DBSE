package com.wink.dbse.service.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Service

@Service
class ChannelNameConvertingService : Converter<String, String> {

    override fun convert(source: String): String {
        return source.toLowerCase()
                .replace("*", "")
                .replace(" +-* *".toRegex(), "-")
    }
}