package com.wink.dbse.service.formatter

interface ILoggedMessageFormatter {

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