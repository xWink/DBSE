package com.wink.dbse.repository

import com.wink.dbse.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import javax.transaction.Transactional

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {

    @Modifying
    @Transactional
    @Query("update UserEntity u set u.wallet = u.wallet + ?2 where u.userId = ?1")
    fun addMoney(userId: Long, amount: Long)

    @Modifying
    @Transactional
    @Query("update UserEntity u set u.wallet = u.wallet - ?2 where u.userId = ?1")
    fun removeMoney(userId: Long, amount: Long)

    @Modifying
    @Transactional
    @Query("update UserEntity u set u.upVotes = u.upVotes + 1 where u.userId = ?1")
    fun addUpVote(userId: Long)

    @Modifying
    @Transactional
    @Query("update UserEntity u set u.downVotes = u.downVotes + 1 where u.userId = ?1")
    fun addDownVote(userId: Long)

    @Modifying
    @Transactional
    @Query("update UserEntity u set u.upVotes = u.upVotes - 1 where u.userId = ?1")
    fun removeUpVote(userId: Long)

    @Modifying
    @Transactional
    @Query("update UserEntity u set u.downVotes = u.downVotes - 1 where u.userId = ?1")
    fun removeDownVote(userId: Long)

    @Modifying
    @Transactional
    @Query("update UserEntity u set u.purchasedRoleId = ?2, u.roleExpiry = ?3 where u.userId = ?1")
    fun setRole(userId: Long, roleId: Long?, roleExpiry: LocalDateTime?)

    @Modifying
    @Transactional
    @Query("update UserEntity u set u.bangStreak = u.bangStreak + 1 where u.userId = ?1")
    fun incrementBangStreak(userId: Long)

    @Modifying
    @Transactional
    @Query("update UserEntity u set u.bangStreak = 0 where u.userId = ?1")
    fun clearBangStreak(userId: Long)


    fun findUserEntitiesByRoleExpiryIsBefore(localDateTime: LocalDateTime) : List<UserEntity>
}