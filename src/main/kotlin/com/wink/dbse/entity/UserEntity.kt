package com.wink.dbse.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "user")
class UserEntity(
        @Id val userId: Long = 0,
        val upVotes: Long = 0,
        val downVotes: Long = 0
)