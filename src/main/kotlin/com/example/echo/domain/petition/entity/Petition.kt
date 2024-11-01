package com.example.echo.domain.petition.entity

import com.example.echo.domain.member.entity.Member
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Petition(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "petition_id")
    val petitionId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)

    var member: Member? = null,

    @Column(name = "title", nullable = false, length = 1000)
    var title: String? = null,

    @Column(name = "content", nullable = false, length = 8000) // 청원서 작성 시 내용 길이 제한 4000 Byte 이지만 인코딩 문제로 잘리는 경우가 있어 8000 확장
    var content: String? = null,

    @Column(name = "summary", length = 4000)
    var summary: String? = null,

    @Column(name = "start_date", nullable = false)

    var startDate: LocalDateTime? = null,

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    var category: Category? = null,

    @Column(name = "original_url", nullable = false)
    var originalUrl: String? = null,

    @Column(name = "related_news")
    var relatedNews: String? = null,

    @Column(name = "likes_count")
    var likesCount: Int = 0,

    @Column(name = "interest_count")
    var interestCount: Int = 0,


    @Column(name = "agree_count") // 청원 객체 생성 시점엔 동의자수 크롤링 데이터를 받아오지 않아 nullable = true 설정

    var agreeCount: Int? = null,

    @ElementCollection
    var likedMemberIds: MutableSet<Long> = mutableSetOf()
) {
    fun changeSummary(summary: String) {
        this.summary = summary
    }

    fun changeAgreeCount(agreeCount: Int) {
        this.agreeCount = agreeCount
    }

    // 좋아요 추가/제거
    fun toggleLike(memberId: Long): Boolean {
        val isLiked = likedMemberIds.contains(memberId)
        if (isLiked) {
            likedMemberIds.remove(memberId)
            likesCount--
        } else {
            likedMemberIds.add(memberId)
            likesCount++
        }
        return !isLiked
    }
}
