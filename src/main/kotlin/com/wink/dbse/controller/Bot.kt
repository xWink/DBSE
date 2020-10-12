package com.wink.dbse.controller

import com.wink.dbse.service.StartupServiceList
import net.dv8tion.jda.api.JDA
import org.springframework.stereotype.Controller

@Controller
class Bot(
        api: JDA,
        startupServiceList: StartupServiceList
) {

    init {
        api.awaitReady()
        for (service in startupServiceList) {
            service.accept(api)
        }
    }
}