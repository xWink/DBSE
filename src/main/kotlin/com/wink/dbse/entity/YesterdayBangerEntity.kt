package com.wink.dbse.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
@Entity
@Table(name = "yesterday")
class YesterdayBangerEntity (
    @Id val userId: Long = 0

)