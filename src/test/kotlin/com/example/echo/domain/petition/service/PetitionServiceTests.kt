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
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class PetitionServiceTests {

    @Autowired
    lateinit var petitionRepository: PetitionRepository

    @Autowired
    lateinit var petitionService: PetitionService

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
    @DisplayName("DB에 존재하는 모든 청원 전체 조회 페이징")
    fun getPetitionsTest() {
        val pageable = PageRequest.of(0, 10)

        val page = petitionService.getPetitions(pageable)

        assertEquals(5, page.totalElements)
        assertEquals("청원 제목 테스트 0", page.first().title)
        assertEquals("청원 제목 테스트 4", page.last().title)
    }

    @Test
    @DisplayName("진행 중인 청원 전체 조회 페이징")
    fun getOngoingPetitionsTest() {
        petitionRepository.save(Petition().apply {
            member = admin
            title = "만료된 청원 제목 테스트"
            content = "만료된 청원 내용 테스트"
            startDate = LocalDateTime.now().minusDays(60L)
            endDate = LocalDateTime.now().minusDays(30L)
            category = Category.POLITICS
            originalUrl = "https://petitions.sample/100"
            likesCount = 100
        })
        val pageable = PageRequest.of(0, 10)

        val page = petitionService.getOngoingPetitions(pageable)

        assertEquals(6, petitionRepository.findAll().size)
        assertEquals(5, page.totalElements, "만료된 청원은 제외하고 진행 중인 청원만 가져옵니다.")
    }

    @Test
    @DisplayName("해당 카테고리 청원 전체 조회 페이징")
    fun getPetitionsByCategoryTest() {
        for (i in 21..29) {
            petitionRepository.save(Petition().apply {
                member = admin
                title = "기타 청원 제목 테스트 $i"
                content = "기타 청원 내용 테스트 $i"
                startDate = LocalDateTime.now()
                endDate = LocalDateTime.now().plusDays(30L)
                category = Category.OTHERS
                originalUrl = "https://petitions.sample/$i"
                likesCount = i
            })
        }
        val pageable = PageRequest.of(1, 5)

        val page = petitionService.getPetitionsByCategory(pageable, Category.OTHERS)

        assertEquals("기타 청원 제목 테스트 26", page.first().title)
        assertEquals(Category.OTHERS, page.first().category)
        assertEquals(9, page.totalElements)
        assertEquals(4, page.content.size)
    }

    @Test
    @DisplayName("청원 만료일 순 5개 조회 - 첫 번째 청원의 종료 날짜가 마지막 청원의 종료 날짜보다 4일 이전이어야 함")
    fun endDatePetitionsTest() {
        val petitions = petitionService.endDatePetitions

        assertEquals(5, petitions.size)

        val firstEndDate = petitions.first().endDate!!.toLocalDate()
        val lastEndDate = petitions.last().endDate!!.toLocalDate()

        assertThat(firstEndDate).isEqualTo(lastEndDate.minusDays(4))
    }

    @Test
    @DisplayName("청원 좋아요 순 5개 조회 - 첫 번째 청원의 좋아요 수가 마지막 청원의 좋아오 수보다 4 커야 함")
    fun likesCountPetitionsTest() {
        val petitions = petitionService.getLikesCountPetitions()

        assertEquals(5, petitions.size)

        val firstLikesCount = petitions.first().likesCount!!
        val lastLikesCount = petitions.last().likesCount!!

        assertThat(firstLikesCount).isEqualTo(lastLikesCount + 4)
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
    @DisplayName("카테고리 선택 시 해당 카테고리 청원 5개 무작위 조회")
    fun getRandomCategoryPetitionsTest() {
        for (i in 0..9) {
            petitionRepository.save(Petition().apply {
                member = admin
                title = "청원 제목 테스트 $i"
                content = "청원 내용 테스트 $i"
                startDate = LocalDateTime.now()
                endDate = LocalDateTime.now().plusDays(30L + i)
                category = Category.POLITICS
                originalUrl = "https://petitions.sample/$i"
                likesCount = i
            })
        }

        val petitions = petitionService.getRandomCategoryPetitions(Category.POLITICS)

        assertEquals(5, petitions.size)
        assertTrue(petitions.all { petition -> petition.category == Category.POLITICS })
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
    @DisplayName("관리자 청원 삭제")
    fun deletePetitionByIdTest() {
        val petition = petitionRepository.save(Petition().apply {
            member = admin
            title = "청원 제목 테스트 삭제"
            content = "청원 내용 테스트 삭제"
            startDate = LocalDateTime.now()
            endDate = LocalDateTime.now().plusDays(30L)
            category = Category.POLITICS
            originalUrl = "https://petitions.sample/10"
            likesCount = 10
        })

        petitionService.deletePetitionById(petition.petitionId!!)

        val exception = assertThrows<PetitionCustomException> {
            petitionService.getPetitionById(petition.petitionId!!)
        }

        assertEquals(ErrorCode.PETITION_NOT_FOUND, exception.errorCode)
    }

    @Test
    @DisplayName("관리자 청원 삭제 - 해당 청원이 없는 경우 예외")
    fun deletePetitionByIdTestNotFound() {
        val wrongPetitionId = 100L

        val exception = assertThrows<PetitionCustomException> {
            petitionService.deletePetitionById(wrongPetitionId)
        }

        assertEquals(ErrorCode.PETITION_NOT_FOUND, exception.errorCode)
    }

    @Test
    @DisplayName("청원 만료일이 지났는지 검증 - 만료일 남은 경우")
    fun isExpiredTestValid() {
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

        val validPetition = petitionRepository.findByIdOrNull(petition.petitionId)
            ?: throw PetitionCustomException(ErrorCode.PETITION_NOT_FOUND)

        val isExpired = petitionService.isExpired(validPetition)

        assertFalse(isExpired, "청원이 아직 만료되지 않았어야 합니다.")
    }

    @Test
    @DisplayName("청원 만료일이 지났는지 검증 - 만료일 지난 경우")
    fun isExpiredTestExpired() {
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

        val isExpired = petitionService.isExpired(expiredPetition)

        assertTrue(isExpired, "청원이 만료 상태여야 합니다.")
    }

    @Test
    @DisplayName("해당 키워드가 제목에 존재하는 청원 검색")
    fun searchPetitionsByTitleTest() {
        val petitions = petitionService.searchPetitionsByTitle("제목 테스트")

        assertEquals(5, petitions.size)

        val petition = petitionService.searchPetitionsByTitle("테스트 0")

        assertEquals(1, petition.size)
        assertEquals("청원 내용 테스트 0", petition.first().content)
    }
}