package com.example.echo.domain.member.repository

import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.entity.Role
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class MemberRepositoryTests {

    @Autowired
    lateinit var memberRepository: MemberRepository

    @BeforeEach
    fun setUp() {
        Member(
            userId = "admin",
            name = "김철수",
            email = "admin@example.com",
            password = "1111",
            phone = "010-1111-1111",
            role = Role.ADMIN
        ).let {
            memberRepository.save(it)
        }
    }

    @Test
    @DisplayName("회원 아이디로 회원 데이터를 조회")
    fun findByUserIdTest() {
        val member = memberRepository.findByUserId("admin") // 조회 성공

        assertNotNull(member)
        assertEquals("김철수", member.name)

        assertNull(memberRepository.findByUserId("wrongUserId")) // 조회 실패
    }

    @Test
    @DisplayName("회원 이메일로 회원 데이터를 조회")
    fun findByEmailTest() {
        val member = memberRepository.findByEmail("admin@example.com") // 조회 성공

        assertNotNull(member)
        assertEquals("김철수", member.name)

        assertNull(memberRepository.findByEmail("wrongEmail")) // 조회 실패
    }

    @Test
    @DisplayName("회원 전화번호로 회원 데이터를 조회")
    fun findByPhoneTest() {
        val member = memberRepository.findByPhone("010-1111-1111") // 조회 성공

        assertNotNull(member)
        assertEquals("김철수", member.name)

        val wrongPhone = "010-9999-9999"
        assertNull(memberRepository.findByPhone(wrongPhone)) // 조회 실패
    }
}