package com.example.echo.domain.member.repository

import com.example.echo.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<Member, Long> {
}