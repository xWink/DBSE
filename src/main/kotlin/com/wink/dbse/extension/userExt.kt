package com.wink.dbse.extension

import net.dv8tion.jda.api.entities.User

fun User.safeName():String {
    return name.replace("""[_-~]""".toRegex()) { return@replace "\\${it.value}" }
}
