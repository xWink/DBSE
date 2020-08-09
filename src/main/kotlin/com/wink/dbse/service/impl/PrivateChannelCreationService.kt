package com.wink.dbse.service.impl

import com.wink.dbse.property.RoleIds
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role

class PrivateChannelCreationService(
        private val roleIds: RoleIds
) {

    fun createChannel(guild: Guild, channelName: String, role: Role) {
        val permissions = listOf(Permission.MESSAGE_READ)
        val everyone: Role = guild.getRolesByName("@everyone", false)[0]
        val cleanChannelName = channelName
                .toLowerCase()
                .replace("\\*", "")
                .replace(" +".toRegex(), "-")

        val channelAction = guild.createTextChannel(cleanChannelName)
                .addPermissionOverride(everyone, null, permissions)
                .addPermissionOverride(role, permissions, null)

        roleIds.globalAccess?.forEach {
            channelAction.addPermissionOverride(guild.getRoleById(it) ?: return@forEach, permissions, null)
        }
    }
}