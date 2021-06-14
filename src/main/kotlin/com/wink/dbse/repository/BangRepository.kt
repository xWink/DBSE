package com.wink.dbse.repository

import com.wink.dbse.entity.BangEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BangRepository : JpaRepository<BangEntity, Long> {


    fun findBangEntitiesById(id: Long): List<BangEntity>

//    /**
//     * Finds the last occurance in a given day
//     */
//    fun findFirstByIdOrderByTimeOccurredAsc(id: Long): BangEntity
}
