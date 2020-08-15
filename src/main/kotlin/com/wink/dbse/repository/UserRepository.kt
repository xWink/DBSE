package com.wink.dbse.repository

import com.wink.dbse.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {

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
}