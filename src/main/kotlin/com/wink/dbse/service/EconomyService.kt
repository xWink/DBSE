package com.wink.dbse.service

import com.jagrosh.jdautilities.oauth2.exceptions.InvalidStateException
import com.wink.dbse.entity.UserEntity
import com.wink.dbse.entity.economy.RoleListing
import com.wink.dbse.repository.UserRepository
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.lang.IllegalStateException
import java.time.LocalDateTime

@Service
class EconomyService(private val userRepository: UserRepository) {

    /**
     * Attempts to make a purchase by removing the cost of the role listing from the buyer's wallet and giving them
     * the purchased role.
     *
     * @throws InvalidStateException if no user with the given buyerId is found, the buyer cannot afford the listing,
     * or the buyer already has a purchased role
     */
    @Throws(InvalidStateException::class)
    fun buy(buyerId: Long, roleListing: RoleListing, guild: Guild) {
        val role: Role = guild.getRoleById(roleListing.roleId!!) ?: throw InvalidStateException("That role does not exist")
        val user: UserEntity = userRepository.findByIdOrNull(buyerId) ?: throw InvalidStateException("User does not exist")

        if (user.wallet < roleListing.cost!!) {
            throw InvalidStateException("You cannot afford to buy this role, you have ${user.wallet} gc")
        }
        if (user.purchasedRoleId != null) {
            throw InvalidStateException("You already have a purchased role, please remove it before buying a new one")
        }

        guild.addRoleToMember(buyerId, role).queue()
        userRepository.setRole(buyerId, roleListing.roleId!!, LocalDateTime.now().plusDays(roleListing.durationDays!!))
    }

    /**
     * Moves money from one user's wallet to another. Verifies that users exist and that the gifter can afford
     * the transaction.
     *
     * @throws IllegalStateException if either user does not exist or the gifter cannot afford the transaction
     */
    @Throws(InvalidStateException::class)
    fun gift(gifterId: Long, gifteeId: Long, amount: Int) {
        val gifter: UserEntity = userRepository.findByIdOrNull(gifterId)
                ?: throw IllegalStateException("User not found with ID $gifterId")

        userRepository.findByIdOrNull(gifteeId) ?: throw IllegalStateException("User not found with ID $gifteeId")

        if (gifter.wallet < amount) {
            throw IllegalStateException("You cannot afford to spend $amount, you only have ${gifter.wallet} gc")
        }
        userRepository.removeMoney(gifterId, amount.toLong())
        userRepository.addMoney(gifteeId, amount.toLong())
    }
}