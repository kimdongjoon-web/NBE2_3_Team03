package com.example.echo.domain.member.service

import com.example.echo.domain.member.dto.request.MemberCreateRequest
import com.example.echo.domain.member.dto.request.MemberLoginRequest
import com.example.echo.domain.member.dto.request.MemberUpdateRequest
import com.example.echo.domain.member.dto.request.ProfileImageUpdateRequest
import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.entity.Role
import com.example.echo.domain.member.repository.MemberRepository
import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.PetitionCustomException
import com.example.echo.global.security.util.JWTUtil
import com.example.echo.log
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * MemberService 통합 테스트
 */
@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class MemberServiceIntegrationTests {

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
            age = 25,
            email = "admin@example.com",
            password = passwordEncoder.encode("1111"),
            phone = "010-1111-1111",
            role = Role.ADMIN,
            avatarImage = "/images/default-avatar.png"
        ).let {
            memberRepository.save(it)
        }

        user = Member(
            userId = "user",
            name = "홍길동",
            age = 44,
            email = "user@example.com",
            password = passwordEncoder.encode("1111"),
            phone = "010-2222-2222",
            role = Role.USER,
            avatarImage = "/images/default-avatar.png"
        ).let {
            memberRepository.save(it)
        }
    }

    @Test
    @DisplayName("회원 아이디와 비밀번호를 통해 로그인")
    fun loginTest() {
        val request = MemberLoginRequest(
            userId = "user",
            password = "1111"
        )

        val tokens = memberService.login(request)

        assertNotNull(tokens["accessToken"])
        assertNotNull(tokens["refreshToken"])
    }

    @Test
    @DisplayName("회원 가입")
    fun createMemberTest() {
        val request = MemberCreateRequest(
            userId = "newUser",
            name = "이영희",
            age =  22,
            email = "newUser@example.com",
            password = "1111",
            phone = "010-3333-3333",
            role = Role.USER
        )

        val response = memberService.createMember(request)
        val savedMember = memberRepository.findByIdOrNull(response.memberId)
            ?: throw PetitionCustomException(ErrorCode.MEMBER_NOT_FOUND)

        assertEquals(response.memberId, savedMember.memberId)
        assertEquals(response.age, savedMember.age)
        assertEquals(response.name, savedMember.name)
    }

    @Test
    @DisplayName("회원 번호를 통해 회원 조회")
    fun getMemberTest() {
        val response = memberService.getMember(admin!!.memberId!!)

        assertEquals("admin", response.userId)
        assertEquals(Role.ADMIN, response.role)
    }

    @Test
    @DisplayName("모든 회원 전체 조회")
    fun getAllMembersTest() {
        val members = memberService.getAllMembers()

        assertEquals(2, members.size)
        assertTrue(members.any { it.userId == "admin" })
        assertTrue(members.any { it.userId == "user" })
    }

    @Test
    @DisplayName("회원 정보 수정 - 이메일, 전화번호, 비밀번호")
    fun updateMemberTest() {
        val request = MemberUpdateRequest(
            email = "update@example.com",
            phone = "010-9999-9999",
            newPassword = "2222",
            currentPassword = "1111"
        )

        val response = memberService.updateMember(user!!.memberId!!, request)   // email, phone, password 수정
        memberRepository
        val updatedMember = memberRepository.findByIdOrNull(user!!.memberId!!)
            ?: throw PetitionCustomException(ErrorCode.MEMBER_NOT_FOUND)

        assertEquals(response.email, updatedMember.email)
        assertEquals(response.phone, updatedMember.phone)
        assertTrue(passwordEncoder.matches("2222", updatedMember.password))
    }

    @Test
    @DisplayName("회원 번호를 통해 회원 삭제")
    fun deleteMemberTest() {
        memberService.deleteMember(user!!.memberId!!)

        assertNull(memberRepository.findByIdOrNull(user!!.memberId))
    }

    @Test
    @DisplayName("회원 번호를 통해 프로필 사진 조회")
    fun getAvatarTest() {
        val path = memberService.getAvatar(user!!.memberId!!)

        assertEquals("/images/default-avatar.png", path)
    }

    @Test
    @DisplayName("회원 번호를 통해 프로필 사진 업데이트")
    fun updateAvatarTest() {
        val newFile = MockMultipartFile(
            "avatarImage",
            "profile.jpg",
            "image/jpeg",
            "이것은 더미 이미지 파일입니다.".toByteArray()
        )
        val request = ProfileImageUpdateRequest(newFile)

        val response = memberService.updateAvatar(user!!.memberId!!, request)

        assertTrue(response.avatarImage!!.endsWith("_${newFile.originalFilename}"))
    }
}