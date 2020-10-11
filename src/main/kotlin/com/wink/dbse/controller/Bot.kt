package com.wink.dbse.controller

import com.wink.dbse.service.StartupService
import net.dv8tion.jda.api.JDA
import org.springframework.stereotype.Controller

@Controller
class Bot(
        api: JDA,
        vararg startupServices: StartupService
) {

    //TODO: MOVE ALL CHECKS TO REMOVE LISTENERS HERE
    init {
        api.awaitReady()
        for (service in startupServices) {
            service.accept(api)
        }
    }
}