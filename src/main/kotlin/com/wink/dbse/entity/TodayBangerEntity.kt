package com.wink.dbse.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
@Entity
@Table(name = "today")
class TodayBangerEntity(@Id val userId: Long = 0)
