package com.example.echo.domain.inquiry.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
@Table(name = "tbl_inquiry")
data class Inquiry (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id", nullable = false, unique = true)
    val inquiryId: Long,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id", nullable = false)
//    var member: Member,

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_category", nullable = false)
    var inquiryCategory: InquiryCategory,

    @Column(name = "inquiry_title", nullable = false)
    var inquiryTitle: String,

    @Column(name = "inquiry_content", length = 2000, nullable = false)
    var inquiryContent: String? = null,

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    val createdDate: LocalDateTime,

    @Column(name = "reply_content", length = 2000) // 문의 등록 시 관리자 답변 null
    var replyContent: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_status", nullable = false)
    var inquiryStatus: InquiryStatus,

    @Column(name = "replied_date")
    var repliedDate: LocalDateTime? = null,

)