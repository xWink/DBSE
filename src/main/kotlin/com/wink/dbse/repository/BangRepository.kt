package com.wink.dbse.repository

import com.wink.dbse.entity.BangEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BangRepository : JpaRepository<BangEntity, Long> {

}
