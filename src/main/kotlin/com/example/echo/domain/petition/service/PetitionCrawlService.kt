package com.example.echo.domain.petition.service

import com.example.echo.domain.member.repository.MemberRepository
import com.example.echo.domain.petition.dto.PetitionCrawlResponse
import com.example.echo.domain.petition.entity.Petition
import com.example.echo.domain.petition.repository.PetitionRepository
import com.example.echo.domain.petition.util.PetitionDataExtractor
import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.PetitionCustomException
import com.example.echo.log
import org.openqa.selenium.*
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import kotlin.NoSuchElementException

@Service
@Transactional
class PetitionCrawlService(

    private val petitionRepository: PetitionRepository,
    private val memberRepository: MemberRepository
) {
    private val driver: WebDriver = ChromeDriver(
        ChromeOptions().apply {
            addArguments("--headless")
        }
    ).apply {
        manage().window().size = Dimension(390, 844)
    }

    private val wait: WebDriverWait = WebDriverWait(driver, Duration.ofSeconds(30))

    // 관리자가 요청한 URL에서 동의 진행 청원을 크롤링한 뒤 결과 반환
    fun dynamicCrawl(id: Long, url: String): List<PetitionCrawlResponse> {
        val crawledData = mutableListOf<PetitionCrawlResponse>()
        try {
            driver.get(url)
            crawlPetitionCardsFromAllPages(crawledData)
            crawlDetailPagesAndSavePetitions(crawledData)
            savePetitionWithMember(crawledData, id)
        } catch (e: Exception) {
            log.error("An error occurred: ", e)
        } finally {
            driver.quit()
        }
        return crawledData
    }

    // 진행 중인 청원 모든 페이지에서 청원 데이터를 크롤링하여 crawledData 리스트에 저장
    private fun crawlPetitionCardsFromAllPages(crawledData: MutableList<PetitionCrawlResponse>) {
        while (true) {
            val petitionElements = loadPetitionElements()
            var countPetition = 0

            for (element in petitionElements) {
                try {
                    val href = getHref(element)
                    if (isPetitionExist(href)) continue

                    val petitionResponse = buildPetitionResponse(element, href)
                    crawledData.add(petitionResponse)
                    countPetition++
                    log.info("success extracting petition: count=$countPetition")
                } catch (e: Exception) {
                    log.error("error extracting petition data: ${e.message}")
                    log.error("count=$countPetition")
                }
            }

            val pageNumbers = parsePageNumbers()
            if (isEndPage(pageNumbers)) break

            navigateToNextPage()
        }
    }

    // 크롤링한 데이터의 상세 페이지를 방문하여 청원 내용을 추출한 뒤 삽입
    private fun crawlDetailPagesAndSavePetitions(crawledData: MutableList<PetitionCrawlResponse>) {
        for (petitionResponse in crawledData) {
            try {
                setContentFromDetailPage(petitionResponse)
            } catch (e: StaleElementReferenceException) {
                log.error("Stale element reference. Retrying petition details fetching for ${petitionResponse.href}")
                setContentFromDetailPage(petitionResponse)
            }
        }
    }

    // 크롤링 데이터를 기반으로 생성한 Petition 객체를 DB에 저장
    private fun savePetitionWithMember(crawledData: MutableList<PetitionCrawlResponse>, id: Long) {
        val member = memberRepository.findByIdOrNull(id)
            ?: throw PetitionCustomException(ErrorCode.MEMBER_NOT_FOUND)

        for (petitionResponse in crawledData) {
            Petition().apply {
                this.member = member
                title = petitionResponse.title
                content = petitionResponse.content
                startDate = petitionResponse.period?.let { PetitionDataExtractor.extractStartDate(it) }
                endDate = petitionResponse.period?.let { PetitionDataExtractor.extractEndDate(it) }
                category = petitionResponse.category?.let { PetitionDataExtractor.convertCategory(it) }
                originalUrl = petitionResponse.href
                agreeCount = petitionResponse.agreeCount?.let { PetitionDataExtractor.extractNumber(it).toInt() }
            }.run {
                petitionRepository.save(this)
            }
        }
    }

    // 웹 페이지 로딩 후, 청원 목록에서 개별 청원 항목을 수집하여 반환
    private fun loadPetitionElements(): List<WebElement> {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".list_card")))
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".item_card")))
        return driver.findElements(By.cssSelector(".item_card"))
    }

    // 개별 청원의 href URL을 반환
    private fun getHref(element: WebElement): String = element.findElement(By.tagName("a")).getAttribute("href")

    // 주어진 URL에 해당하는 청원 데이터가 이미 DB에 존재하는지 확인
    private fun isPetitionExist(href: String): Boolean = petitionRepository.findByUrl(href) > 0

    // 크롤링한 WebElement에서 청원 데이터를 추출하여 PetitionCrawlResponse 객체 생성
    private fun buildPetitionResponse(element: WebElement, href: String): PetitionCrawlResponse =
        PetitionCrawlResponse().apply {
            title = element.findElement(By.cssSelector(".desc")).text
            period = element.findElement(By.cssSelector(".period")).text
            category = element.findElement(By.cssSelector(".category")).text
            agreeCount = element.findElement(By.cssSelector(".count")).text
            this.href = href
            content = null
        }

    // 현재 페이지 번호와 전체 페이지 수를 반환
    private fun parsePageNumbers(): Pair<Int, Int> {
        return try {
            val pageText = driver.findElement(By.cssSelector("div.mobile_paging span")).text
            val pattern = Regex("(\\d+) / (\\d+)")
            val matchResult = pattern.find(pageText)

            val currentPageNum = matchResult?.groupValues?.get(1)?.toInt() ?: run {
                log.error("현재 페이지 파싱 실패: $pageText")
                -1
            }
            val totalPageNum = matchResult?.groupValues?.get(2)?.toInt() ?: run {
                log.error("전체 페이지 파싱 실패: $pageText")
                -1
            }
            currentPageNum to totalPageNum
        } catch (e: NoSuchElementException) {
            log.error("페이지 정보를 찾을 수 없습니다. ${e.message}")
            -1 to -1
        } catch (e: NumberFormatException) {
            println("페이지 번호 파싱에 실패했습니다. ${e.message}")
            -1 to -1
        }
    }

    // 현재 페이지가 마지막 페이지인지 확인
    private fun isEndPage(pageNumbers: Pair<Int, Int>): Boolean = pageNumbers.first == pageNumbers.second

    // 다음 페이지로 이동
    private fun navigateToNextPage() {
        try {
            clickNextButton()
            waitForPageLoad()
        } catch (e: StaleElementReferenceException) {
            log.error("StaleElementReferenceException while paging: ", e)
        } catch (e: TimeoutException) {
            log.error("TimeoutException while paging: ", e)
        } catch (e: NoSuchElementException) {
            log.error("NoSuchElementException while paging: ", e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    // 다음 페이지 버튼이 활성화되면 클릭
    private fun clickNextButton() = wait.until(
        ExpectedConditions.elementToBeClickable(
            By.cssSelector("button.btn.next-button")
        )
    ).click()

    // 페이지가 완전히 로드될 때까지 대기
    private fun waitForPageLoad() = wait.until {
        (driver as JavascriptExecutor).executeScript("return document.readyState") == "complete"
    }

    // 상세 페이지에서 청원 내용을 가져와서 주어진 PetitionCrawlResponse에 설정
    private fun setContentFromDetailPage(response: PetitionCrawlResponse) {
        try {
            while (response.content.isNullOrEmpty()) {
                fetchContent(response)
            }
        } catch (e: TimeoutException) {
            throw PetitionCustomException(ErrorCode.SELENIUM_TIMEOUT)
        } catch (e: NoSuchElementException) {
            throw PetitionCustomException(ErrorCode.SELENIUM_NO_ELEMENT_FOUND)
        } catch (e: Exception) {
            throw PetitionCustomException(ErrorCode.SELENIUM_UNKNOWN_ERROR)
        }
    }

    // 주어진 URL에서 청원 내용 추출해 삽입
    private fun fetchContent(response: PetitionCrawlResponse) {
        driver.get(response.href)
        waitForPageLoad()

        val contentText = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".pre.contentTxt"))
        ).text.trim()

        if (contentText.isNotEmpty()) {
            response.content = contentText
        } else {
            log.error("청원 내용 삽입 에러. URL: ${response.href}")
        }
    }

    // 동의자 수를 추출하여 업데이트. 최대 3회 재시도
    fun fetchAgreeCount(url: String): Int {
        driver.get(url)
        waitForPageLoad()
        var retries = 3

        while (retries-- > 0) {
            try {
                return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".count"))
                ).text.let {
                    PetitionDataExtractor.extractNumber(it).toInt()
                }
            } catch (e: TimeoutException) {
                log.warn("Timeout while waiting for the agree count element. Retrying... Remaining attempts: $retries")
            } catch (e: NoSuchElementException) {
                log.warn("NoSuchElementException: Unable to find the agree count element on URL: $url. Retrying... Remaining attempts: $retries")
            }
        }

        log.error("Failed to fetch agree count after multiple attempts for URL: $url")
        return -1 // 모든 재시도에서 실패한 경우 -1 반환
    }
}