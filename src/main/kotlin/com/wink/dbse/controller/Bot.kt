package com.wink.dbse.controller

import com.wink.dbse.service.startup.IStartupService
import net.dv8tion.jda.api.JDA
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
class Bot @Autowired constructor(
        api: JDA,
        vararg startupServices: IStartupService
) {

    //TODO: MOVE ALL CHECKS TO REMOVE LISTENERS HERE
    init {
        api.awaitReady()
        for (service in startupServices) {
            service.accept(api)
        }
    }
}