package com.example.echo.domain.inquiry.service

import com.example.echo.domain.inquiry.dto.request.InquiryPageRequest
import com.example.echo.domain.inquiry.dto.request.InquiryCreateRequest
import com.example.echo.domain.inquiry.dto.response.InquiryResponse
import com.example.echo.domain.inquiry.entity.InquiryCategory
import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.entity.Role
import com.example.echo.domain.member.repository.MemberRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertFailsWith

@SpringBootTest
class InquiryServiceTests {

    @Autowired
    private lateinit var inquiryService: InquiryService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    @DisplayName("1:1 문의 생성")
    fun testCreateInquiry() {
        val inquiryRequest = InquiryCreateRequest(
            inquiryCategory = InquiryCategory.MEMBER,
            inquiryTitle = "문의제목11",
            inquiryContent = "문의내용11"
        )

        val createdInquiry = inquiryService.createInquiry(inquiryRequest, 1L)

        assertNotNull(createdInquiry)
        println(createdInquiry)
    }

    @Test
    @DisplayName("모든 회원 1:1 문의 상세 조회")
    fun testGetInquiryById() {
        val inquiryId = 1L

        // 정상 조회
        val foundInquiry = inquiryService.getInquiryById(inquiryId, 1L)
        assertNotNull(foundInquiry)

        // 예외 발생 테스트
        val exception = assertFailsWith<RuntimeException> {
            inquiryService.getInquiryById(100L, 1L)
        }
        assertEquals("1:1 문의를 찾을 수 없습니다.", exception.message)
    }

    @Test
    @DisplayName("ADMIN 1:1 문의 전체 조회")
    fun testFindAllForAdmin() {
        // 2페이지 5사이즈 조회. DB엔 문의 데이터 총 14개 존재.
        val member = Member(
            userId = "member4",
            name = "ddd",
            email = "d@d.com",
            password = "111",
            phone = "010-1234-5678",
            avatarImage = "ddd.png",
            role = Role.ADMIN
        )

        memberRepository.save(member)
        val inquiryPageRequest = InquiryPageRequest(pageNumber = 2)

        val inquiriesAdmin: Page<InquiryResponse> =
            inquiryService.getInquiriesByMemberRole(inquiryPageRequest, member.memberId!!)

        assertNotNull(inquiriesAdmin)
        assertEquals(13, inquiriesAdmin.totalElements)
        assertEquals(3, inquiriesAdmin.totalPages)
        assertEquals(1, inquiriesAdmin.number)
        assertEquals(5, inquiriesAdmin.size)
        assertEquals(5, inquiriesAdmin.content.size)
    }

    @Test
    @DisplayName("USER 등록한 1:1 문의 전체 조회")
    fun testFindAllForUser() {
        // 2페이지 5사이즈 조회. DB엔 memberId = 4로 문의 데이터 총 9개 존재.
        val memberId = 4L
        val inquiryPageRequest = InquiryPageRequest(pageNumber = 2)

        val inquiriesUser: Page<InquiryResponse> =
            inquiryService.getInquiriesByMemberRole(inquiryPageRequest, memberId)

        assertNotNull(inquiriesUser)
        assertEquals(9, inquiriesUser.totalElements)
        assertEquals(2, inquiriesUser.totalPages)
        assertEquals(1, inquiriesUser.number)
        assertEquals(5, inquiriesUser.size)
        assertEquals(4, inquiriesUser.content.size)
    }

}
