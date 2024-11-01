package com.example.echo.domain.inquiry.entity

import com.example.echo.domain.member.entity.Member
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "inquiry")
@EntityListeners(AuditingEntityListener::class)
class Inquiry (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id", nullable = false, unique = true)
    val inquiryId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member = Member(),

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_category", nullable = false)
    var inquiryCategory: InquiryCategory,

    @Column(name = "inquiry_title", nullable = false)
    var inquiryTitle: String = "",

    @Column(name = "inquiry_content", length = 2000, nullable = false)
    var inquiryContent: String = "",

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    val createdDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "reply_content", length = 2000) // 문의 등록 시 관리자 답변 null
    var replyContent: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_status", nullable = false)
    var inquiryStatus: InquiryStatus = InquiryStatus.PENDING,

    @Column(name = "replied_date")
    var repliedDate: LocalDateTime? = null,

    ) {

    fun changeReplyContent(replyContent: String?) {
        this.replyContent = replyContent
        this.inquiryStatus = InquiryStatus.RESOLVED
        this.repliedDate = LocalDateTime.now()
    }

    override fun toString(): String {
        return "Inquiry(inquiryId=$inquiryId, member=${member.memberId}, inquiryCategory=$inquiryCategory," +
                "inquiryTitle=$inquiryTitle, inquiryContent=$inquiryContent,createdDate=$createdDate," +
                "replyContent=$replyContent, inquiryStatus=$inquiryStatus, repliedDate=$repliedDate)"
    }

}