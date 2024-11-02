package com.example.echo.domain.member.service

import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.entity.Role
import com.example.echo.domain.member.repository.MemberRepository
import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.PetitionCustomException
import com.example.echo.global.security.util.JWTUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * MemberService 단위 테스트
 */
@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class MemberServiceUnitTests {

    @Autowired
    lateinit var memberService: MemberService

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var jwtUtil: JWTUtil

    private var admin: Member? = null
    private var user: Member? = null

    @BeforeEach
    fun setUp() {
        admin = Member(
            userId = "admin",
            name = "김철수",
            email = "admin@example.com",
            password = passwordEncoder.encode("1111"),
            phone = "010-1111-1111",
            role = Role.ADMIN
        ).let {
            memberRepository.save(it)
        }

        user = Member(
            userId = "user",
            name = "홍길동",
            email = "user@example.com",
            password = passwordEncoder.encode("1111"),
            phone = "010-2222-2222",
            role = Role.USER
        ).let {
            memberRepository.save(it)
        }
    }

    @Test
    @DisplayName("회원 아이디로 회원 데이터를 조회")
    fun findMemberByUserIdTest() {
        val member = memberService.findMemberByUserId(user!!.userId) // 조회 성공

        assertNotNull(member)
        assertEquals("홍길동", member.name)

        assertThrows<PetitionCustomException> {
            memberService.findMemberByUserId("wrongUserId") // 조회 실패
        }.also {
            assertEquals(ErrorCode.MEMBER_NOT_FOUND, it.errorCode)
        }
    }

    @Test
    @DisplayName("회원 번호로 회원 데이터를 조회")
    fun findMemberByIdTest() {
        val member = memberService.findMemberById(user!!.memberId!!) // 조회 성공

        assertNotNull(member)
        assertEquals("홍길동", member.name)

        assertThrows<PetitionCustomException> {
            memberService.findMemberById(user!!.memberId!! + 1) // 조회 실패
        }.also {
            assertEquals(ErrorCode.MEMBER_NOT_FOUND, it.errorCode)
        }
    }

    @Test
    @DisplayName("회원 아이디 중복 검증 - 중복인 경우 USERID_ALREADY_EXISTS 예외")
    fun checkUserIdDuplicateTest() {
        assertDoesNotThrow {
            memberService.checkUserIdDuplicate("newUserId") // 중복 X
        }

        assertThrows<PetitionCustomException> {
            memberService.checkUserIdDuplicate(user!!.userId) // 중복 O
        }.also {
            assertEquals(ErrorCode.USERID_ALREADY_EXISTS, it.errorCode)
        }
    }

    @Test
    @DisplayName("회원 이메일 중복 검증 - 중복인 경우 EMAIL_ALREADY_EXISTS 예외")
    fun checkEmailDuplicateTest() {
        assertDoesNotThrow {
            memberService.checkEmailDuplicate("newEmail") // 중복 X
        }

        assertThrows<PetitionCustomException> {
            memberService.checkEmailDuplicate(user!!.email) // 중복 O
        }.also {
            assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, it.errorCode)
        }
    }

    @Test
    @DisplayName("회원 전화번호 중복 검증 - 중복인 경우 PHONE_ALREADY_EXISTS 예외")
    fun checkPhoneDuplicateTest() {
        val newPhone = "010-9999-9999"

        assertDoesNotThrow {
            memberService.checkPhoneDuplicate(newPhone) // 중복 X
        }

        assertThrows<PetitionCustomException> {
            memberService.checkPhoneDuplicate(user!!.phone) // 중복 O
        }.also {
            assertEquals(ErrorCode.PHONE_ALREADY_EXISTS, it.errorCode)
        }
    }

    @Test
    @DisplayName("회원 비밀번호 유효성 검증 - 유효하지 않은 경우 INVALID_PASSWORD 예외")
    fun validatePasswordTest() {
        val validPassword = "1111"

        assertDoesNotThrow {
            memberService.validatePassword(validPassword, user!!.password) // 유효 O
        }

        val invalidPassword = "9999"

        assertThrows<PetitionCustomException> {
            memberService.validatePassword(invalidPassword, user!!.password) // 유효 X
        }.also {
            assertEquals(ErrorCode.INVALID_PASSWORD, it.errorCode)
        }
    }

    @Test
    @DisplayName("JWT 토큰 생성 검증 & 관리자/사용자에 따른 유효성 검증")
    fun makeTokenTest() {
        val adminTokens = memberService.makeToken(admin!!) // 관리자

        assertEquals(2, adminTokens.size)
        val adminClaims = jwtUtil.validateToken(adminTokens["accessToken"]!!)
        assertEquals("ADMIN", adminClaims["role"])

        val userTokens = memberService.makeToken(user!!) // 사용자

        assertEquals(2, userTokens.size)
        val userClaims = jwtUtil.validateToken(userTokens["accessToken"]!!)
        assertEquals("USER", userClaims["role"])
    }
}