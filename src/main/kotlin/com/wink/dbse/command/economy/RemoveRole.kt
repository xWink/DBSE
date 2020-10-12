package com.wink.dbse.command.economy

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import com.wink.dbse.command.ConfirmableCommand
import com.wink.dbse.repository.UserRepository
import com.wink.dbse.service.Messenger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class RemoveRole(
        private val messenger: Messenger,
        private val userRepository: UserRepository,
        waiter: EventWaiter
) : ConfirmableCommand(
        messenger,
        "No refunds. Are you sure you want to remove your current purchased role?",
        waiter
) {

    init {
        name = "removerole"
        help = "Remove your current purchased role"
    }

    override fun onConfirmationReceived(event: CommandEvent) {
        val authorId = event.author.idLong
        val user = userRepository.findByIdOrNull(authorId) ?: return
        val roleId = user.purchasedRoleId
        if (roleId == null) {
            messenger.sendMessage(event.channel, "You do not have a purchased role")
            return
        }
        val guild = event.guild
        val role = guild.getRoleById(user.purchasedRoleId) ?: return

        userRepository.setRole(authorId, null, null)
        guild.removeRoleFromMember(authorId, role).queue()
        messenger.sendMessage(event.channel, "Your role has been removed")
        logger.info("Successfully executed removerole command by user \"${event.author.name}\"")
    }

    private companion object {
        @JvmStatic private val logger: Logger = LoggerFactory.getLogger(RemoveRole::class.java)
    }
}