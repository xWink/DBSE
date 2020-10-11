package com.wink.dbse.service

import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

interface LoggedMessageFormatter {

    /**
     * Formats a message's information so that it can be cleanly printed.
     * @param timeSentSecs time in seconds since epoch that the message was sent at
     * @param channel name of the channel that the message was sent in
     * @param author name of the author of the message
     * @param content what was in the message
     * @return a neatly formatted String containing information about a message
     */
    fun format(timeSentSecs: Long, channel: String?, author: String?, content: String): String
}

@Service
class LoggedMessageFormattingService : LoggedMessageFormatter {

    companion object {
        private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm:ss")
        private val EST: ZoneId = ZoneId.of("America/Toronto")
    }

    /**
     * Formats a message to be logged in the following pattern: \[DateTime] <Channel> {Author}: Message
     * The DateTime is formatted with the pattern "MMM dd yyyy, HH:mm:ss" and is set to EST time zone.
     *
     * Example: [Jul 18 2020, 13:38:39] <general> {Wink}: test
     *
     * @param timeSentSecs time in seconds since epoch that the message was sent at
     * @param channel name of the channel that the message was sent in
     * @param author name of the author of the message
     * @param content content of the message
     * @return a formatted String containing data about a message
     */
    override fun format(timeSentSecs: Long, channel: String?, author: String?, content: String): String {
        val localDateTime: String = Instant.ofEpochSecond(timeSentSecs).atZone(EST).toLocalDateTime().format(FORMATTER)
        return "[${localDateTime}] <${channel ?: "Unknown Channel"}> {${author ?: "Unknown Author"}}: " + content
    }
}