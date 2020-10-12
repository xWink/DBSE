package com.wink.dbse.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "bang")
class BangEntity(
        @Id
        @GeneratedValue
        val id: Long = 0,
        val userId: Long = 0,
        val timeOccurred: LocalDateTime? = null,
        val result: Int = -1
)
