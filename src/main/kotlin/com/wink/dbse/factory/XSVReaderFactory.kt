package com.wink.dbse.factory

import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
object XSVReaderFactory {

    @Bean
    fun tsvReader(): CsvReader = csvReader {
        quoteChar = '"'
        delimiter = '\t'
        escapeChar = '\\'
    }
}