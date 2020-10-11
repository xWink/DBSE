package com.wink.dbse.entity

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "user")
class UserEntity(
        @Id val userId: Long = 0,
        val upVotes: Long = 0,
        val downVotes: Long = 0,
        val wallet: Long = 0,
        val purchasedRoleId: Long? = null,
        val roleExpiry: LocalDateTime? = null
)
