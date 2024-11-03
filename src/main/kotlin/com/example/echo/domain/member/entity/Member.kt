package com.example.echo.domain.member.entity


import com.example.echo.domain.inquiry.entity.Inquiry
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "member")
@EntityListeners(AuditingEntityListener::class)
data class Member(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false, unique = true)
    val memberId: Long? = null,

    @Column(name = "user_Id", nullable = false, unique = true)
    var userId: String = "",

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "age", nullable = false)
    var age: Int = 0,

    @Column(name = "email", nullable = false, unique = true)
    var email: String = "",

    @Column(name = "password", nullable = false)
    var password: String = "",

    @Column(name = "phone", nullable = false, length = 20)
    var phone: String = "",

    @Column(name = "avatar_image")
    var avatarImage: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    var role: Role = Role.USER,

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    val createdDate: LocalDateTime = LocalDateTime.now(),

    @ElementCollection
    @CollectionTable(name = "member_interests", joinColumns = [JoinColumn(name = "member_id")])
    @Column(name = "petition_id")
    val interestList: MutableList<Long> = mutableListOf(),

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL])
    val inquiryList: MutableList<Inquiry> = mutableListOf(),
) {

    // 문의 추가 메서드
    fun addInquiry(inquiry: Inquiry) {
        inquiryList.add(inquiry)
    }


    fun getPayload(): Map<String, Any> {
        return mapOf(
            "memberId" to (memberId ?: 0L), // null인 경우 기본값 0L 제공
            "userId" to userId,
            "name" to name,
            "email" to email,
            "role" to role // 기존 문자열 반환에서 수정
        )
    }

    override fun toString(): String {
        return "Member(memberId=$memberId, " +
                "userId='$userId', " +
                "name='$name', " +
                "email='$email', " +
                "password='$password', " +
                "phone='$phone', " +
                "avatarImage='$avatarImage', " +
                "role=$role, " +
                "createdDate=$createdDate, " +
                "interestListSize=${interestList.size}, " +
                "inquiryListSize=${inquiryList.size})"

    }
}
