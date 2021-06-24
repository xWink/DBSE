package com.wink.dbse.entity

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "bang")
class BangEntity(
        @Id
        @GeneratedValue
        val id: Long? = null,
        val userId: Long? = null,
        val timeOccurred: LocalDateTime? = null,
        val result: Int? = null
)
