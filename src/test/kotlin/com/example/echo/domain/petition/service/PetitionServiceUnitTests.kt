package com.example.echo.domain.petition.service

import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.entity.Role
import com.example.echo.domain.member.repository.MemberRepository
import com.example.echo.domain.petition.entity.Category
import com.example.echo.domain.petition.entity.Petition
import com.example.echo.domain.petition.repository.PetitionRepository
import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.PetitionCustomException
import com.example.echo.log
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class PetitionServiceUnitTests {

    @Autowired
    lateinit var petitionRepository: PetitionRepository

    @Autowired
    lateinit var petitionService: PetitionService

    @Autowired
    lateinit var memberRepository : MemberRepository

    @Autowired
    lateinit var summarizationService: SummarizationService

    private var admin: Member? = null

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
    }

    @Test
    @DisplayName("청원 내용 요약 기능이 적절한 요약 결과를 반환하는지 검증")
    fun getSummarizedTextTest() {
        val content = """
                국토교통부는 생활숙박시설에 대하여 주거규제를 추진함에 따라 2021년 5월 건축법 시행령을 개정하여 개정이후 분양되는 건축물 및 기존 생활숙박시설 건축물(소급적용) 전부에 대하여 숙박업 신고의무를 부과하고 주거사용시 건축물의 용도위반에 해당하여 이행강제금을 부과할 수 있도록 하는 정책을 발표하였습니다. 이에 따라 국토부는 2021년 10월 14일부터 2023년 10월 14일까지 생활숙박시설의 오피스텔 용도변경 유도정책을 추진한다고 하였으나 실제 용도변경을 완성한 곳은 1% 남짓에 불과하였습니다.

                이러한 상황으로 인하여 생활숙박시설에 거주하고 있는 소유자, 주거 이용 목적으로 분양을 받은 수분양자들은 이행강제금을 부담할 수도 없고 대체 주거시설을 마련할 경제적 여유도 없기에 주거권을 박탈당할 위기에 놓였으며, 생계를 위협받으며 가정이 해체될 위험한 지경에 이르고 있습니다. 또한 건축 중인 생활숙박시설단지들 역시 수분양권자들은 주거가능 시설로 오인하게 하여 분양한 시행사를 상대로 분양권 취소 소송을 할 수 밖에 없고, 이로 인해 중도금 상환 연체, 잔급 미납 등으로 개인 신용불량 위험에 놓이며, 시행사와 시공사, 부동산 금융기관 역시 줄줄이 파산의 위협과 공포에 놓인 상황입니다. 오피스텔 용도변경을 추진하고 있는 생활숙박시설 단지의 관할 지방자치단체 역시 용도변경에 관련된 국토부의 종합적인 지침도 없이  적극 행정조치를 하지 못하고 민원을 해결할 수 없어 지방행정에 대한 불만누적으로 고통받고 있으며 현실에 맞지 않는 모순된 생활숙박시설 주거규제는 일파만파 사회적 문제를 야기하고 있습니다. 

                따라서 현실적이고 근본적인 문제 해결을 위해서는 현행 주택법 시행령에 규정에 되어 있는 준주택 제도를 활용하여 사실상 주거시설로 사용가능하고 사용되어지고 있는 생활숙박시설을 국민의 주거안정과 건축시설의 효율적인 이용을 위한 법상제도인 준주택으로 편입하여 현실에 맞는 제도정비를 통해 안정적인 주거생활로 가정을 유지할 수 있도록 노력해주실 것을 요청드리고자 청원에 이르게 되었습니다.
            """.trimIndent()

        val summary = summarizationService.getSummarizedText(content)

        assertNotEquals("summary는 비어있지 않아야 합니다.", "", summary)
        assertTrue("summary는 4000자를 초과할 수 없습니다.") {summary.length <= 4000}
        log.info("요약 결과: $summary")
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
        val petitions = petitionService.likesCountPetitions

        assertEquals(5, petitions.size)

        val firstLikesCount = petitions.first().likesCount!!
        val lastLikesCount = petitions.last().likesCount!!

        assertThat(firstLikesCount).isEqualTo(lastLikesCount + 4)
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

        val exception = org.junit.jupiter.api.assertThrows<PetitionCustomException> {
            petitionService.getPetitionById(petition.petitionId!!)
        }

        assertEquals(ErrorCode.PETITION_NOT_FOUND, exception.errorCode)
    }

    @Test
    @DisplayName("관리자 청원 삭제 - 해당 청원이 없는 경우 예외")
    fun deletePetitionByIdTestNotFound() {
        val wrongPetitionId = 100L

        val exception = org.junit.jupiter.api.assertThrows<PetitionCustomException> {
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