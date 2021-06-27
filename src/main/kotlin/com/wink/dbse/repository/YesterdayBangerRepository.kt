package com.wink.dbse.repository

import com.wink.dbse.entity.YesterdayBangerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface YesterdayBangerRepository: JpaRepository<YesterdayBangerEntity, Long>  {
    fun findFirstByUserId(userId: Long): YesterdayBangerEntity?
}