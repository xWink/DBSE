package com.wink.dbse.factory

import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class EventWaiterFactory {

    private val waiter = EventWaiter()

    @Bean
    fun eventWaiter(): EventWaiter {
        return waiter
    }
}