package com.example.echo.domain.petition.repository

import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.entity.Role
import com.example.echo.domain.member.repository.MemberRepository
import com.example.echo.domain.petition.entity.Category
import com.example.echo.domain.petition.entity.Petition
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class PetitionRepositoryTests {

    @Autowired
    lateinit var petitionRepository: PetitionRepository

    @Autowired
    lateinit var memberRepository: MemberRepository

    private var admin: Member? = null

    @BeforeEach
    fun setUp() {
        admin = Member(
            userId = "admin",
            name = "김철수",
            age = 25,
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
    @DisplayName("특정 카테고리의 청원 목록 페이징 조회 테스트")
    fun findByCategoryTest() {
        val pageable = PageRequest.of(0, 2)
        val category = Category.POLITICS

        val page = petitionRepository.findByCategory(pageable, category)

        assertEquals(1, page.totalElements, "페이징한 청원의 수는 1")
        assertEquals(category, page.content.firstOrNull()?.category, "해당 청원 카테고리는 ${category.description}.")
    }

    @Test
    @DisplayName("DB에 없는 카테고리로 청원 목록 페이징 조회 시 실패")
    fun findByCategoryTestFail() {
        val pageable = PageRequest.of(0, 2)
        val wrongCategory = Category.OTHERS

        val page = petitionRepository.findByCategory(pageable, wrongCategory)

        assertEquals(0, page.totalElements, "잘못된 카테고리로 조회했을 때 청원의 수는 0")
    }

    @Test
    @DisplayName("주어진 originalUrl에 해당하는 청원 수 조회")
    fun findByUrlTest() {
        val url = "https://petitions.sample/0"
        val wrongUrl = "https://petitions.sample/100"

        assertEquals(1, petitionRepository.findByUrl(url))
        assertEquals(0, petitionRepository.findByUrl(wrongUrl))
    }

    @Test
    @DisplayName("청원 만료일 순 5개 조회 - 첫 번째 청원의 종료 날짜가 마지막 청원의 종료 날짜보다 4일 이전이어야 함")
    fun getEndDatePetitionsTest() {
        val pageable = PageRequest.of(0, 5)

        val petitions = petitionRepository.getEndDatePetitions(pageable)

        assertEquals(5, petitions.size)

        val firstEndDate = petitions.first().endDate!!.toLocalDate()
        val lastEndDate = petitions.last().endDate!!.toLocalDate()

        assertThat(firstEndDate).isEqualTo(lastEndDate.minusDays(4))
    }

    @Test
    @DisplayName("청원 좋아요 순 5개 조회 - 첫 번째 청원의 좋아요 수가 마지막 청원의 좋아오 수보다 4 커야 함")
    fun getLikesCountPetitionsTest() {
        val pageable = PageRequest.of(0, 5)

        val petitions = petitionRepository.getLikesCountPetitions(pageable)

        assertEquals(5, petitions.size)

        val firstLikesCount = petitions.first().likesCount!!
        val lastLikesCount = petitions.last().likesCount!!

        assertThat(firstLikesCount).isEqualTo(lastLikesCount + 4)
    }

    @Test
    @DisplayName("카테고리 선택 시 해당 카테고리 청원 5개 무작위 조회")
    fun getCategoryPetitionsInRandomOrderTest() {
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
        val pageable = PageRequest.of(0, 5)

        val petitions = petitionRepository.getCategoryPetitionsInRandomOrder(Category.POLITICS, pageable)

        assertEquals(5, petitions.size)
        assertTrue(petitions.all { petition -> petition.category == Category.POLITICS })
    }

    @Test
    @DisplayName("제목이 특정 문자열을 포함하는 청원 조회 테스트")
    fun findByTitleContainingIgnoreCaseTest() {
        petitionRepository.save(Petition().apply {
            member = admin
            title = "특정 제목 테스트"
            content = "청원 내용 테스트"
            startDate = LocalDateTime.now()
            endDate = LocalDateTime.now().plusDays(30)
            category = Category.POLITICS
            originalUrl = "https://petitions.sample/specific"
            likesCount = 5
        })

        val petitions = petitionRepository.findByTitleContainingIgnoreCase("특정")

        assertEquals(1, petitions.size)
        assertTrue(petitions.all { it.title!!.contains("특정", ignoreCase = true) })
    }

    @Test
    @DisplayName("진행 중인 청원 목록 조회 테스트")
    fun findAllOngoingTest() {
        val pageable = PageRequest.of(0, 5)

        val petitions = petitionRepository.findAllOngoing(pageable)

        assertTrue(petitions.totalElements > 0)
        assertTrue(petitions.content.all { it.endDate!!.isAfter(LocalDateTime.now()) })
    }

    @Test
    @DisplayName("모든 진행 중인 청원 조회 테스트")
    fun findAllActiveTest() {
        val activePetitions = petitionRepository.findAllActive()

        assertTrue(activePetitions.all { it.endDate!!.isAfter(LocalDateTime.now()) })
    }
}