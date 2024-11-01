package com.example.echo.domain.petition.service

import com.example.echo.domain.member.dto.request.MemberCreateRequest
import com.example.echo.domain.member.entity.Role
import com.example.echo.domain.member.service.MemberService
import com.example.echo.log
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class PetitionUtilServiceTests {

    @Autowired
    lateinit var petitionCrawlService: PetitionCrawlService

    @Autowired
    lateinit var memberService: MemberService

    @Autowired
    lateinit var summarizationService: SummarizationService

    @BeforeEach
    fun setUp() {
        MemberCreateRequest(
            "admin",
            "김철수",
            "admin@example.com",
            "1111",
            "010-1111-1111",
            null,
            Role.ADMIN
        ).run {
            memberService.createMember(this)
        }
    }

    @Test
    @DisplayName("청원 사이트에서 전체 청원 수와 크롤링된 리스트의 수가 일치하는지 검증")
    fun dynamicCrawlTest() {
        val memberId = 1L
        val url = "https://petitions.assembly.go.kr/proceed/onGoingAll"

        val totalCount = petitionCrawlService.fetchTotalCount(url)  // 전체 청원 수
        val crawledData = petitionCrawlService.dynamicCrawl(memberId, url)  // 크롤링한 청원 리스트

        assertEquals(totalCount, crawledData.size)
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
        assertTrue("summary는 4000자를 초과할 수 없습니다.", ) {summary.length <= 4000}
        log.info("요약 결과: $summary")
    }
}