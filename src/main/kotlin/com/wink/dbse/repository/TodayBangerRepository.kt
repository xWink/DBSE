package com.wink.dbse.repository

import com.wink.dbse.entity.TodayBangerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TodayBangerRepository: JpaRepository<TodayBangerEntity, Long>  {
    fun findFirstByUserId(userId: Long): TodayBangerEntity?
}