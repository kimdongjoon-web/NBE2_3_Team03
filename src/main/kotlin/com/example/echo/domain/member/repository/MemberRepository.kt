package com.example.echo.domain.member.repository

import com.example.echo.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Long> {

    fun findByUserId(userId: String): Member?

    fun findByEmail(email: String): Member?

    fun findByPhone(phone: String): Member?
}
