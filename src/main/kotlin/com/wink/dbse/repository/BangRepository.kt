package com.wink.dbse.repository

import com.wink.dbse.entity.BangEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BangRepository : JpaRepository<BangEntity, Long> {

    /**
     * Generates a list of BangEntity sorted by
     * @param id the Target users id
     * @return the List of Bang Entities corresponding to single User sorted by occurrences
     */
    fun findBangEntitiesByIdOrderByTimeOccurredAsc(id: Long) : List<BangEntity>

}
