package com.wink.dbse.service.impl

import com.wink.dbse.property.RoleIds
import kotlinx.coroutines.coroutineScope
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Service

@Service
class PrivateChannelCreationService(private val roleIds: RoleIds) {

    suspend fun createPrivateChannel(guild: Guild, channelName: String) = coroutineScope {
        val permissions = listOf(Permission.MESSAGE_READ)
        val everyone: Role = guild.getRolesByName("@everyone", false)[0]
        val channelAction = guild.createTextChannel(channelName)
                .addPermissionOverride(everyone, null, permissions)

        roleIds.globalAccess?.forEach {
            channelAction.addPermissionOverride(guild.getRoleById(it) ?: return@forEach, permissions, null)
        }

        channelAction.queue()
    }

    suspend fun createPrivateChannel(guild: Guild, channelName: String, role: Role) = coroutineScope {
        val permissions = listOf(Permission.MESSAGE_READ)
        val everyone: Role = guild.getRolesByName("@everyone", false)[0]
        val channelAction = guild.createTextChannel(channelName)
                .addPermissionOverride(everyone, null, permissions)
                .addPermissionOverride(role, permissions, null)

        roleIds.globalAccess?.forEach {
            channelAction.addPermissionOverride(guild.getRoleById(it) ?: return@forEach, permissions, null)
        }

        channelAction.queue()
    }
}