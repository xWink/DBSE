package com.wink.dbse.controller

import net.dv8tion.jda.api.JDA
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
class Bot @Autowired constructor(private val api: JDA)