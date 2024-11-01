package com.example.echo.domain.petition.entity

import com.example.echo.domain.member.entity.Member
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "petition")
data class Petition(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "petition_id")
    val petitionId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Column(name = "title", nullable = false, length = 1000)
    val title: String,

    @Column(name = "content", nullable = false, length = 4000)
    val content: String,

    @Column(name = "summary", length = 4000)
    var summary: String? = null,

    @Column(name = "start_date", nullable = false)
    val startDate: LocalDateTime,

    @Column(name = "end_date", nullable = false)
    val endDate: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    val category: Category,

    @Column(name = "original_url", nullable = false)
    val originalUrl: String,

    @Column(name = "related_news")
    val relatedNews: String? = null,

    @Column(name = "likes_count")
    var likesCount: Int = 0,

    @Column(name = "interest_count")
    var interestCount: Int = 0,

    @Column(name = "agree_count")
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
