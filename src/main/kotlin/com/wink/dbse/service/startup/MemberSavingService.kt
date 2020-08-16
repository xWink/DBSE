package com.wink.dbse.service.startup

import com.wink.dbse.entity.UserEntity
import com.wink.dbse.repository.UserRepository
import net.dv8tion.jda.api.JDA
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MemberSavingService @Autowired constructor(private val repository: UserRepository) : IStartupService {

    /**
     * Saves all cached members as users in the database.
     */
    override fun accept(jda: JDA) {
        jda.users.forEach { if (repository.findById(it.idLong).isEmpty) repository.save(UserEntity(it.idLong)) }
    }
}