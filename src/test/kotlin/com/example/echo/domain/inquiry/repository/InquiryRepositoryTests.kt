package com.example.echo.domain.inquiry.repository

import com.example.echo.domain.inquiry.dto.response.InquiryResponse
import com.example.echo.domain.inquiry.entity.Inquiry
import com.example.echo.domain.inquiry.entity.InquiryCategory
import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.entity.Role
import com.example.echo.domain.member.repository.MemberRepository
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class InquiryRepositoryTests {

    @Autowired
    private lateinit var inquiryRepository: InquiryRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    @DisplayName("1:1 문의 저장")
    fun testSaveInquiry() {
        val member = Member(
            userId = "member2",
            name = "bbb",
            email = "b@b.com",
            password = "111",
            phone = "010-1234-5678",
            avatarImage = "bbb.png",
            role = Role.USER
        )

        val savedMember = memberRepository.save(member)

        val inquiry = Inquiry(
            member = savedMember,
            inquiryCategory = InquiryCategory.MEMBER,
            inquiryTitle = "문의제목b",
            inquiryContent = "문의내용b"
        )

        val savedInquiry = inquiryRepository.save(inquiry)

        assertNotNull(savedInquiry)
        println(savedInquiry)
    }

    @Test
    @DisplayName("1:1 문의 1개 조회")
    fun testFindInquiry() {
        val inquiryId = 1L

        val foundInquiry = inquiryRepository.findById(inquiryId)

        assertNotNull(foundInquiry)
        assertEquals(1L, foundInquiry.get().inquiryId)
        println(foundInquiry)
    }

    @Test
    @DisplayName("ADMIN의 모든 1:1 문의 조회 페이징")
    fun testFindAllInquiriesAdmin() {
        // 1페이지 5사이즈 조회. DB엔 문의 데이터 총 13개 존재.
        val pageable: Pageable = PageRequest.of(1, 5, Sort.by("inquiryId").descending())

        val inquiriesAdmin: Page<Inquiry> = inquiryRepository.findAllInquiriesAdmin(pageable)

        assertNotNull(inquiriesAdmin)
        assertEquals(13, inquiriesAdmin.totalElements)
        assertEquals(3, inquiriesAdmin.totalPages)
        assertEquals(1, inquiriesAdmin.number)
        assertEquals(5, inquiriesAdmin.size)
        assertEquals(5, inquiriesAdmin.content.size)
    }

    @Test
    @DisplayName("USER의 모든 1:1 문의 조회 페이징")
    fun testFindAllInquiriesUser() {
        // 1페이지 5사이즈 조회. DB엔 memberId = 4 로 문의 데이터 총 9개 존재.
        val memberId = 4L
        val pageable: Pageable = PageRequest.of(1, 5, Sort.by("inquiryId").descending())

        val inquiriesUser: Page<Inquiry> = inquiryRepository.findAllInquiriesUser(memberId, pageable)

        assertNotNull(inquiriesUser)
        assertEquals(9, inquiriesUser.totalElements)
        assertEquals(2, inquiriesUser.totalPages)
        assertEquals(1, inquiriesUser.number)
        assertEquals(5, inquiriesUser.size)
        assertEquals(4, inquiriesUser.content.size)
    }

}
