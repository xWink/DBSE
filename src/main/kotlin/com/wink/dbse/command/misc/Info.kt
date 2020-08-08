package com.wink.dbse.command.misc

import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.wink.dbse.service.IMessenger
import net.dv8tion.jda.api.EmbedBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

@Component
class Info @Autowired constructor(
        private val messenger: IMessenger,
        private val tsvReader: CsvReader
) : Command() {

    @Value("classpath:courses.tsv")
    private val courses: Resource? = null

    init {
        name = "info"
        help = "Provides information about a given course ID"
        arguments = "<course ID>"
    }

    override fun execute(event: CommandEvent) {
        val courseId = event.args.toUpperCase().replace("\\*|\\s+".toRegex(), "")
        val found = tsvReader.open(courses?.file ?: return) {
            readAllWithHeaderAsSequence().find {
                courseId == it["Course Title"]
                        ?.split("\\s+".toRegex())
                        ?.get(0)
                        ?.replace("\\*".toRegex(), "")
            }
        }

        if (found == null) {
            messenger.sendMessage(event.channel, "Could not find a course with that ID")
        } else {
            messenger.sendMessage(event.channel, embedCourseData(found).build())
        }
    }

    private fun embedCourseData(data: Map<String, String>): EmbedBuilder {
        val eb = EmbedBuilder()
        eb.setTitle(data["Course Title"])
        eb.addField("Restrictions:", data["Restrictions"]?.replace("Restriction(s): ", "")?.replace("*", "\\*"), false)
        eb.addField("Prerequisites:", data["Prerequisites"]?.replace("Prerequisite(s): ", "")?.replace("*", "\\*"), false)
        eb.addField("Description: ", data["Descriptions"], false)
        return eb
    }
}