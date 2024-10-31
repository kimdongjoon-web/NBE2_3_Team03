package com.example.echo.domain.member.repository

import com.example.echo.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun findByUserId(userId: String): Optional<Member>

    fun findByEmail(email: String): Optional<Member>

    fun findByPhone(phone: String): Optional<Member>
}
