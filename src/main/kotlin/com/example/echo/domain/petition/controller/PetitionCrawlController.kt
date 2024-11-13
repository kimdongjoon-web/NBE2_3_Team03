package com.example.echo.domain.petition.controller

import com.example.echo.domain.petition.dto.response.PetitionCrawlResponse
import com.example.echo.domain.petition.service.PetitionCrawlService
import com.example.echo.global.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/petitions")
@Tag(name = "Petition Crawling Controller", description = "청원 크롤링 API")
class PetitionCrawlController(

    private val petitionCrawlService: PetitionCrawlService
) {
    @Value("\${webdriver.chrome.driver}")
    private lateinit var chromeDriverPath: String

    @PostConstruct
    fun init() {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath)
    }

    // 진행중인 청원 전체 크롤링 후 데이터 저장
    @Operation(summary = "청원 홈페이지 크롤링", description = "모든 청원 데이터를 크롤링한 후, 해당 데이터를 청원 데이터베이스에 삽입합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/petitioncrawl/{id}")
    fun crawlAndSaveAllPetitions(
        @Parameter(description = "크롤링할 관리자 회원의 ID") @PathVariable id: Long
    ): ResponseEntity<ApiResponse<List<PetitionCrawlResponse>>> =
        petitionCrawlService.dynamicCrawl(id, "https://petitions.assembly.go.kr/proceed/onGoingAll")
            .let { ResponseEntity.ok(ApiResponse.success(it)) }
}