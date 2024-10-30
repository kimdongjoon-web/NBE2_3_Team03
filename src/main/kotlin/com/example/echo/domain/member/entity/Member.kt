package com.example.echo.domain.member.entity


import com.example.echo.domain.inquiry.entity.Inquiry
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "member")
@EntityListeners(AuditingEntityListener::class)
class Member(
    var userId: String,

    var name: String,

    var email: String,

    var password: String,

    var phone: String,

    var avatarImage: String? = null,

    @Enumerated(EnumType.STRING)
    var role: Role,

    @CreatedDate
    val createdDate: LocalDateTime = LocalDateTime.now(), // 기본값으로 현재 시간 설정


    @ElementCollection
    @CollectionTable(name = "member_interests", joinColumns = [JoinColumn(name = "member_id")])
    @Column(name = "petition_id")

    val interestList: MutableList<Long> = mutableListOf(),

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL])
    val inquiryList: MutableList<Inquiry> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val memberId: Long? = null
) {
    init {
        require(userId.isNotBlank()) { "유저 아이디는 비어 있을 수 없습니다" }
        require(name.isNotBlank()) { "이름은 비어 있을 수 없습니다" }
    }

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
            "role" to role.toString() // Enum을 문자열로 변환
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
