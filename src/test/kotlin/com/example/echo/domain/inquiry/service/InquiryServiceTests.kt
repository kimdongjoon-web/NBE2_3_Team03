package com.example.echo.domain.inquiry.service

import com.example.echo.domain.inquiry.dto.request.InquiryPageRequest
import com.example.echo.domain.inquiry.dto.request.InquiryCreateRequest
import com.example.echo.domain.inquiry.dto.response.InquiryResponse
import com.example.echo.domain.inquiry.entity.Inquiry
import com.example.echo.domain.inquiry.entity.InquiryCategory
import com.example.echo.domain.inquiry.repository.InquiryRepository
import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.entity.Role
import com.example.echo.domain.member.repository.MemberRepository
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertFailsWith

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
@Transactional
class InquiryServiceTests {

    @Autowired
    private lateinit var inquiryService: InquiryService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var inquiryRepository: InquiryRepository

    private lateinit var testMember: Member
    private lateinit var adminMember: Member

    @BeforeEach
    fun setUp() {
        // 일반 사용자 생성
        testMember = memberRepository.save(Member(
            userId = "testUser",
            name = "Test User",
            email = "test@test.com",
            password = "password",
            phone = "010-1234-5678",
            avatarImage = "test.png",
            role = Role.USER
        ))

        // 관리자 생성
        adminMember = memberRepository.save(Member(
            userId = "admin",
            name = "Admin User",
            email = "admin@test.com",
            password = "password",
            phone = "010-8765-4321",
            avatarImage = "admin.png",
            role = Role.ADMIN
        ))

        // 테스트 문의 데이터 생성
        val inquiries = mutableListOf<Inquiry>()

        // 일반 사용자의 문의 9개 생성
        repeat(9) { i ->
            inquiries.add(Inquiry(
                member = testMember,
                inquiryCategory = InquiryCategory.MEMBER,
                inquiryTitle = "사용자 문의 ${i + 1}",
                inquiryContent = "사용자 문의 내용 ${i + 1}"
            ))
        }

        // 다른 사용자의 문의 4개 생성
        repeat(4) { i ->
            inquiries.add(Inquiry(
                member = adminMember,
                inquiryCategory = InquiryCategory.MEMBER,
                inquiryTitle = "관리자 문의 ${i + 1}",
                inquiryContent = "관리자 문의 내용 ${i + 1}"
            ))
        }

        inquiryRepository.saveAll(inquiries)
    }

    @Test
    @DisplayName("1:1 문의 생성")
    fun testCreateInquiry() {
        val inquiryRequest = InquiryCreateRequest(
            inquiryCategory = InquiryCategory.MEMBER,
            inquiryTitle = "새로운 문의",
            inquiryContent = "새로운 문의 내용"
        )

        val createdInquiry = inquiryService.createInquiry(inquiryRequest, testMember.memberId!!)

        assertNotNull(createdInquiry)
        assertEquals("새로운 문의", createdInquiry.inquiryTitle)
        assertEquals("새로운 문의 내용", createdInquiry.inquiryContent)
    }

    @Test
    @DisplayName("모든 회원 1:1 문의 상세 조회")
    fun testGetInquiryById() {
        // 첫 번째 문의 가져오기
        val savedInquiry = inquiryRepository.findAll().first()

        // 정상 조회
        val foundInquiry = inquiryService.getInquiryById(savedInquiry.inquiryId!!, testMember.memberId!!)
        assertNotNull(foundInquiry)
        assertEquals(savedInquiry.inquiryTitle, foundInquiry.inquiryTitle)

        // 예외 발생 테스트
        val exception = assertFailsWith<RuntimeException> {
            inquiryService.getInquiryById(99999L, testMember.memberId!!)
        }
        assertEquals("1:1 문의를 찾을 수 없습니다.", exception.message)
    }

    @Test
    @DisplayName("ADMIN 1:1 문의 전체 조회")
    fun testFindAllForAdmin() {
        val inquiryPageRequest = InquiryPageRequest(pageNumber = 2)

        val inquiriesAdmin: Page<InquiryResponse> =
            inquiryService.getInquiriesByMemberRole(inquiryPageRequest, adminMember.memberId!!)

        assertNotNull(inquiriesAdmin)
        assertEquals(13, inquiriesAdmin.totalElements)  // 총 13개 문의 (9 + 4)
        assertEquals(3, inquiriesAdmin.totalPages)      // 5개씩 페이징하면 3페이지
        assertEquals(1, inquiriesAdmin.number)          // 현재 1페이지
        assertEquals(5, inquiriesAdmin.size)            // 페이지 크기 5
        assertEquals(5, inquiriesAdmin.content.size)    // 현재 페이지 내용 5개
    }

    @Test
    @DisplayName("USER 등록한 1:1 문의 전체 조회")
    fun testFindAllForUser() {
        val inquiryPageRequest = InquiryPageRequest(pageNumber = 2)

        val inquiriesUser: Page<InquiryResponse> =
            inquiryService.getInquiriesByMemberRole(inquiryPageRequest, testMember.memberId!!)

        assertNotNull(inquiriesUser)
        assertEquals(9, inquiriesUser.totalElements)   // 사용자의 문의 9개
        assertEquals(2, inquiriesUser.totalPages)      // 5개씩 페이징하면 2페이지
        assertEquals(1, inquiriesUser.number)          // 현재 1페이지
        assertEquals(5, inquiriesUser.size)            // 페이지 크기 5
        assertEquals(4, inquiriesUser.content.size)    // 현재 페이지 내용 4개
    }
}