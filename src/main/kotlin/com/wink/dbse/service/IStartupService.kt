package com.wink.dbse.service

import net.dv8tion.jda.api.JDA
import java.util.function.Consumer

/**
 * Service whose run method is executed after the Bot is instantiated.
 */
interface IStartupService : Consumer<JDA>