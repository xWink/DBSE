package com.wink.dbse.extension

import net.dv8tion.jda.api.entities.Message

fun Message.attachmentProxy(): String {
    return attachments.map { it.proxyUrl }.getOrElse(0) { "" }
}