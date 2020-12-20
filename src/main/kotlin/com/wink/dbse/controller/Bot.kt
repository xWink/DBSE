package com.wink.dbse.controller

import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import com.wink.dbse.service.StartupServiceList
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Controller

@Controller
class Bot(
        api: JDA,
        client: CommandClient,
        waiter: EventWaiter,
        vararg eventListeners: ListenerAdapter,
        startupServiceList: StartupServiceList
) {

    init {
        api.addEventListener(client)
        api.addEventListener(waiter)
        api.addEventListener(*eventListeners)
        api.awaitReady()
        for (service in startupServiceList) {
            service.accept(api)
        }
    }
}