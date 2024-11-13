package com.example.echo.domain.inquiry.repository

import com.example.echo.domain.inquiry.dto.response.InquiryResponse
import com.example.echo.domain.inquiry.entity.Inquiry
import com.example.echo.domain.inquiry.entity.InquiryCategory
import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.entity.Role
import com.example.echo.domain.member.repository.MemberRepository
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DataJpaTest
class InquiryRepositoryTests {

    @Autowired
    private lateinit var inquiryRepository: InquiryRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    private lateinit var testMember1: Member
    private lateinit var testMember2: Member

    @BeforeEach
    fun setUp() {
        // 테스트 멤버 생성
        testMember1 = memberRepository.save(Member(
            userId = "member1",
            name = "aaa",
            email = "a@a.com",
            password = "111",
            phone = "010-1111-1111",
            avatarImage = "aaa.png",
            role = Role.USER
        ))

        testMember2 = memberRepository.save(Member(
            userId = "member2",
            name = "bbb",
            email = "b@b.com",
            password = "111",
            phone = "010-2222-2222",
            avatarImage = "bbb.png",
            role = Role.USER
        ))

        // 테스트 문의 데이터 생성
        val inquiries = mutableListOf<Inquiry>()

        // member1의 문의 9개 생성
        repeat(9) { i ->
            inquiries.add(Inquiry(
                member = testMember1,
                inquiryCategory = InquiryCategory.MEMBER,
                inquiryTitle = "문의제목 ${i + 1}",
                inquiryContent = "문의내용 ${i + 1}"
            ))
        }

        // member2의 문의 4개 생성
        repeat(4) { i ->
            inquiries.add(Inquiry(
                member = testMember2,
                inquiryCategory = InquiryCategory.MEMBER,
                inquiryTitle = "다른 회원 문의제목 ${i + 1}",
                inquiryContent = "다른 회원 문의내용 ${i + 1}"
            ))
        }

        inquiryRepository.saveAll(inquiries)
    }

    @Test
    @DisplayName("1:1 문의 저장")
    fun testSaveInquiry() {
        val inquiry = Inquiry(
            member = testMember2,
            inquiryCategory = InquiryCategory.MEMBER,
            inquiryTitle = "새로운 문의제목",
            inquiryContent = "새로운 문의내용"
        )

        val savedInquiry = inquiryRepository.save(inquiry)

        assertNotNull(savedInquiry)
        assertEquals("새로운 문의제목", savedInquiry.inquiryTitle)
        assertEquals("새로운 문의내용", savedInquiry.inquiryContent)
    }

    @Test
    @DisplayName("1:1 문의 1개 조회")
    fun testFindInquiry() {
        // 첫 번째 문의 조회
        val inquiry = inquiryRepository.findAll().first()
        val foundInquiry = inquiryRepository.findById(inquiry.inquiryId!!)

        assertNotNull(foundInquiry)
        assertTrue(foundInquiry.isPresent)
        assertEquals(inquiry.inquiryId, foundInquiry.get().inquiryId)
    }

    @Test
    @DisplayName("ADMIN의 모든 1:1 문의 조회 페이징")
    fun testFindAllInquiriesAdmin() {
        val pageable: Pageable = PageRequest.of(1, 5, Sort.by("inquiryId").descending())

        val inquiriesAdmin: Page<Inquiry> = inquiryRepository.findAllInquiriesAdmin(pageable)

        assertNotNull(inquiriesAdmin)
        assertEquals(13, inquiriesAdmin.totalElements) // 총 13개 문의 (9 + 4)
        assertEquals(3, inquiriesAdmin.totalPages)     // 5개씩 페이징하면 3페이지
        assertEquals(1, inquiriesAdmin.number)         // 현재 1페이지
        assertEquals(5, inquiriesAdmin.size)           // 페이지 크기 5
        assertEquals(5, inquiriesAdmin.content.size)   // 현재 페이지 내용 5개
    }

    @Test
    @DisplayName("USER의 모든 1:1 문의 조회 페이징")
    fun testFindAllInquiriesUser() {
        val pageable: Pageable = PageRequest.of(1, 5, Sort.by("inquiryId").descending())

        val inquiriesUser: Page<Inquiry> = inquiryRepository.findAllInquiriesUser(testMember1.memberId!!, pageable)

        assertNotNull(inquiriesUser)
        assertEquals(9, inquiriesUser.totalElements)  // member1의 문의 9개
        assertEquals(2, inquiriesUser.totalPages)     // 5개씩 페이징하면 2페이지
        assertEquals(1, inquiriesUser.number)         // 현재 1페이지
        assertEquals(5, inquiriesUser.size)           // 페이지 크기 5
        assertEquals(4, inquiriesUser.content.size)   // 현재 페이지 내용 4개
    }

    @AfterEach
    fun cleanup() {
        inquiryRepository.deleteAll()
        memberRepository.deleteAll()
    }
}