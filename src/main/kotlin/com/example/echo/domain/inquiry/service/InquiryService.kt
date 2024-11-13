package com.example.echo.domain.inquiry.service

import com.example.echo.domain.inquiry.dto.request.AdminAnswerRequest
import com.example.echo.domain.inquiry.dto.request.InquiryCreateRequest
import com.example.echo.domain.inquiry.dto.request.InquiryPageRequest
import com.example.echo.domain.inquiry.dto.request.InquiryUpdateRequest
import com.example.echo.domain.inquiry.dto.response.InquiryResponse
import com.example.echo.domain.inquiry.entity.Inquiry
import com.example.echo.domain.inquiry.repository.InquiryRepository
import com.example.echo.domain.member.entity.Role
import com.example.echo.domain.member.service.MemberService
import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.PetitionCustomException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InquiryService(

    private val memberService: MemberService,
    private val inquiryRepository: InquiryRepository
){
    @Transactional
    fun createInquiry(inquiryCreateRequest: InquiryCreateRequest, memberId: Long): InquiryResponse {
        val foundMember = memberService.findMemberById(memberId)
        val createdInquiry = inquiryCreateRequest.toEntity(foundMember)
        val savedInquiry = inquiryRepository.save(createdInquiry)
        return InquiryResponse.from(savedInquiry)
    }

    fun getInquiryById(inquiryId: Long, memberId: Long): InquiryResponse {
        val foundMember = memberService.findMemberById(memberId)
        val foundInquiry = findInquiryById(inquiryId)

        if (foundMember.role == Role.USER) {
            validateUserInquiryAccess(memberId, foundInquiry)
        }
        return InquiryResponse.from(foundInquiry)
    }

    fun getInquiriesByMemberRole(inquiryRequest: InquiryPageRequest, memberId: Long): Page<InquiryResponse> {
        val foundMember = memberService.getMember(memberId)
        return if (foundMember.role == Role.ADMIN) {
            findAllForAdmin(inquiryRequest.getPageable()) // ADMIN 모든 문의 조회
        } else {
            findAllForUser(memberId, inquiryRequest.getPageable()) // USER 개인 모든 문의 조회
        }
    }

    // USER 본인 1:1 문의 수정
    @Transactional
    fun updateInquiry(inquiryId: Long?, inquiryRequest: InquiryUpdateRequest, memberId: Long?): InquiryResponse {
        val foundInquiry = findInquiryById(inquiryId!!)
        validateUserInquiryAccess(memberId!!, foundInquiry)
        inquiryRequest.updateInquiry(foundInquiry)
        return InquiryResponse.from(inquiryRepository.save(foundInquiry))
    }

    // ADMIN/USER 본인 1:1 문의 삭제
    @Transactional
    fun deleteInquiry(inquiryId: Long?, memberId: Long) {
        val foundMember = memberService.getMember(memberId)
        val foundInquiry = findInquiryById(inquiryId!!)
        if (foundMember.role == Role.USER) {
            validateUserInquiryAccess(memberId, foundInquiry)
        }
        inquiryRepository.delete(foundInquiry)
    }

    // 관리자 문의 답변
    @Transactional
    fun addAnswer(inquiryId: Long?, replyContent: String?) {
        val inquiry = findInquiryById(inquiryId!!)
        inquiry.changeReplyContent(replyContent)
        inquiryRepository.save(inquiry)
    }

    // ADMIN 1:1 문의 답변 삭제
    @Transactional
    fun deleteAnswer(inquiryId: Long?) {
        val inquiry = findInquiryById(inquiryId!!)
        inquiry.deleteReplyContent()
        inquiryRepository.save(inquiry)
    }


    private fun findInquiryById(inquiryId: Long): Inquiry {
        return inquiryRepository.findById(inquiryId)
            .orElseThrow { PetitionCustomException(ErrorCode.INQUIRY_NOT_FOUND) }

    }

    // ADMIN 모든 문의 조회
    private fun findAllForAdmin(pageable: Pageable): Page<InquiryResponse> {
        val inquiriesPage: Page<Inquiry> = inquiryRepository.findAllInquiriesAdmin(pageable)
        return inquiriesPage.map{ InquiryResponse.from(it)}
    }

    // USER 본인 문의 조회
    private fun findAllForUser(memberId: Long, pageable: Pageable): Page<InquiryResponse> {
        val inquiriesPage: Page<Inquiry> = inquiryRepository.findAllInquiriesUser(memberId, pageable)
        return inquiriesPage.map{ InquiryResponse.from(it)}
    }

    // USER인 경우 해당 문의 작성자 본인 검증
    private fun validateUserInquiryAccess(memberId: Long, inquiry: Inquiry) {
        if (inquiry.member.memberId != memberId) {
            throw PetitionCustomException(ErrorCode.INQUIRY_ACCESS_DENIED)
        }
    }

}