package com.example.echo.domain.petition.service

import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.entity.Role
import com.example.echo.domain.member.repository.MemberRepository
import com.example.echo.domain.petition.dto.request.PetitionRequestDto
import com.example.echo.domain.petition.entity.Category
import com.example.echo.domain.petition.entity.Petition
import com.example.echo.domain.petition.repository.PetitionRepository
import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.PetitionCustomException
import com.example.echo.log
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class PetitionServiceIntegrationTests {

    @Autowired
    lateinit var petitionRepository: PetitionRepository

    @Autowired
    lateinit var petitionService: PetitionService

    @Autowired
    lateinit var petitionCrawlService: PetitionCrawlService

    @Autowired
    lateinit var memberRepository: MemberRepository

    private var admin: Member? = null
    private var request: PetitionRequestDto? = null

    @BeforeEach
    fun setUp() {
        admin = Member(
            userId = "admin",
            name = "김철수",
            email = "admin@example.com",
            password = "1111",
            phone = "010-1111-1111",
            role = Role.ADMIN
        ).let {
            memberRepository.save(it)
        }

        for (i in 0..4) {
            petitionRepository.save(Petition().apply {
                member = admin
                title = "청원 제목 테스트 $i"
                content = "청원 내용 테스트 $i"
                startDate = LocalDateTime.now()
                endDate = LocalDateTime.now().plusDays(30L + i)
                category = Category.entries.toTypedArray()[i]
                originalUrl = "https://petitions.sample/$i"
                likesCount = i
            })
        }

        request = PetitionRequestDto(
            memberId = admin!!.memberId!!,
            title = "청원 요청 제목 테스트",
            content = "청원 요청 내용 테스트",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusDays(30),
            category = Category.POLITICS,
            originalUrl = "https://petitions.sample/0"
        )
    }

    @Test
    @DisplayName("관리자가 청원을 등록할 경우, 청원 데이터는 총 6개")
    fun createPetitionTest() {
        val savedPetition = petitionService.createPetition(request!!)

        assertEquals("청원 요청 제목 테스트", savedPetition.title)
        assertEquals(6, petitionRepository.findAll().size)
    }

    @Test
    @DisplayName("청원 단건 조회 - 요약이 없는 경우 요약 생성해서 반환")
    fun getPetitionByIdNoSummaryTest() {
        val petition = petitionRepository.save(Petition().apply {
            member = admin
            title = "청원 제목 테스트"
            content = "청원 내용 테스트"
            startDate = LocalDateTime.now()
            endDate = LocalDateTime.now().plusDays(30L)
            category = Category.POLITICS
            originalUrl = "https://petitions.sample/i"
            likesCount = 30
        })

        assertEquals("청원 제목 테스트", petition.title)
        assertNotEquals("요약은 비어있지 않아야 합니다.", "", petition.summary)
        log.info("요약 결과: ${petition.summary}")
    }

    @Test
    @DisplayName("청원 단건 조회 - 요약이 있는 경우 요약 그대로 반환")
    fun getPetitionByExistingSummaryTest() {
        val summaryPetition = petitionRepository.save(Petition().apply {
            member = admin
            title = "요약 청원 제목 테스트"
            content = "요약 청원 내용 테스트"
            startDate = LocalDateTime.now()
            endDate = LocalDateTime.now().plusDays(30L)
            category = Category.POLITICS
            originalUrl = "https://petitions.sample/10"
            likesCount = 10
            summary = "청원 요약 존재"
        })

        val foundPetition = petitionService.getPetitionById(summaryPetition.petitionId!!)

        assertEquals("요약 청원 제목 테스트", foundPetition.title)
        assertEquals("청원 요약 존재", foundPetition.summary)
    }

    @Test
    @DisplayName("청원 단건 조회 - 해당 청원이 없는 경우 예외")
    fun getPetitionByIdTestNotFound() {
        val wrongPetitionId = 100L

        val exception = assertThrows<PetitionCustomException> {
            petitionService.getPetitionById(wrongPetitionId)
        }

        assertEquals(ErrorCode.PETITION_NOT_FOUND, exception.errorCode)
    }

    @Test
    @DisplayName("청원 단건 조회 - 해당 청원 만료 기간이 지난 경우 예외")
    fun getPetitionByIdTestExpired() {
        val expiredPetition = petitionRepository.save(Petition().apply {
            member = admin
            title = "만료된 청원 제목 테스트"
            content = "만료된 청원 내용 테스트"
            startDate = LocalDateTime.now().minusDays(60L)
            endDate = LocalDateTime.now().minusDays(30L)
            category = Category.POLITICS
            originalUrl = "https://petitions.sample/100"
            likesCount = 100
        })

        val exception = assertThrows<PetitionCustomException> {
            petitionService.getPetitionById(expiredPetition.petitionId!!)
        }

        assertEquals(ErrorCode.PETITION_EXPIRED, exception.errorCode)
    }

    @Test
    @DisplayName("청원 좋아요 추가/제거 검증")
    fun toggleLikeOnPetitionTest() {
        val petition = petitionRepository.save(Petition().apply {
            member = admin
            title = "청원 제목 테스트 좋아요"
            content = "청원 내용 테스트 좋아요"
            startDate = LocalDateTime.now()
            endDate = LocalDateTime.now().plusDays(30L)
            category = Category.POLITICS
            originalUrl = "https://petitions.sample/10"
            likesCount = 4
        })

        assertEquals(4, petition.likesCount)
        assertThat(petition.likedMemberIds).doesNotContain(admin!!.memberId!!)  // 좋아요 목록에 없는지 검증

        val likeMessage = petitionService.toggleLikeOnPetition(petition.petitionId!!, admin!!.memberId!!)

        assertEquals("좋아요가 추가되었습니다.", likeMessage)
        assertEquals(5, petition.likesCount)
        assertThat(petition.likedMemberIds).contains(admin!!.memberId!!) // 좋아요 추가 후 있는지 검증

        val cancelMessage = petitionService.toggleLikeOnPetition(petition.petitionId!!, admin!!.memberId!!)

        assertEquals("좋아요가 제거되었습니다.", cancelMessage)
        assertEquals(4, petition.likesCount)
        assertThat(petition.likedMemberIds).doesNotContain(admin!!.memberId!!)  // 좋아요 목록에 다시 없는지 검증
    }

    @Test
    @DisplayName("관리자 청원 수정")
    fun updatePetitionTest() {
        val petition = petitionRepository.save(Petition().apply {
            member = admin
            title = "청원 제목 테스트 수정"
            content = "청원 내용 테스트 수정"
            startDate = LocalDateTime.now()
            endDate = LocalDateTime.now().plusDays(30L)
            category = Category.POLITICS
            originalUrl = "https://petitions.sample/10"
            likesCount = 10
        })

        petitionService.updatePetition(petition.petitionId!!, request!!)

        assertEquals("청원 요청 제목 테스트", petition.title)
        assertNotEquals("청원 내용 테스트 0", petition.content, "새 요청 내용으로 변해야 합니다.")
    }

    @Test
    @DisplayName("관리자 청원 수정 - 해당 청원이 없는 경우 예외 ")
    fun updatePetitionTestNotFound() {
        val wrongPetitionId = 1000L

        val exception = assertThrows<PetitionCustomException> {
            petitionService.updatePetition(wrongPetitionId, request!!)
        }

        assertEquals(ErrorCode.PETITION_NOT_FOUND, exception.errorCode)
    }

    @Test
    @DisplayName("청원 사이트에서 전체 청원 수와 크롤링된 리스트의 수가 일치하는지 검증")
    fun dynamicCrawlTest() {
        val url = "https://petitions.assembly.go.kr/proceed/onGoingAll"

        val totalCount = petitionCrawlService.fetchTotalCount(url)  // 전체 청원 수
        val crawledData = petitionCrawlService.dynamicCrawl(admin!!.memberId!!, url)  // 크롤링한 청원 리스트

        assertEquals(totalCount, crawledData.size)
        assertEquals(totalCount, petitionRepository.findAll().size - 5)
    }
}