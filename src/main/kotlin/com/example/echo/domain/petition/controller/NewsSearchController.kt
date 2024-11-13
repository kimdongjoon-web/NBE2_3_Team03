package com.example.echo.domain.petition.controller

import com.example.echo.domain.petition.dto.request.NewsSearchRequest
import com.example.echo.domain.petition.dto.response.NewsResponseDto
import com.example.echo.domain.petition.dto.response.PetitionDetailResponseDto
import com.example.echo.domain.petition.service.NewsSearchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/search")
@Tag(name = "News Controller", description = "뉴스 관리 API")
class NewsSearchController(
    private val newsSearchService: NewsSearchService
) {

    @GetMapping("/news")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "뉴스 검색 및 저장", description = "검색어로 뉴스를 검색하고 결과를 DB에 저장합니다.")
    fun searchNews(
        @RequestParam query: String,
        @RequestParam(required = false, defaultValue = "3") display: Int,
        @RequestParam(required = false, defaultValue = "1") start: Int,
        @RequestParam(required = false, defaultValue = "sim") sort: String
    ): ResponseEntity<List<NewsResponseDto>> {
        val result = newsSearchService.searchNews(
            NewsSearchRequest(
                query = query,
                display = display,
                start = start,
                sort = NewsSearchRequest.SortType.fromValue(sort)
                    ?: throw IllegalArgumentException("Invalid sort type: $sort")
            )
        )
        return ResponseEntity.ok(result.map { NewsResponseDto.from(it) })
    }


    @GetMapping("/news/search")
    @Operation(summary = "뉴스 검색", description = "검색어로 뉴스를 검색하고 결과를 반환합니다. (DB 저장하지 않음)")
    fun searchNewsOnly(
        @RequestParam query: String,
        @RequestParam(required = false, defaultValue = "3") display: Int,
        @RequestParam(required = false, defaultValue = "1") start: Int,
        @RequestParam(required = false, defaultValue = "sim") sort: String
    ): ResponseEntity<List<NewsResponseDto>> {
        val result = newsSearchService.searchNewsOnly(
            NewsSearchRequest(
                query = query,
                display = display,
                start = start,
                sort = NewsSearchRequest.SortType.fromValue(sort)
                    ?: throw IllegalArgumentException("Invalid sort type: $sort")
            )
        )
        return ResponseEntity.ok(result.map { NewsResponseDto.from(it) })
    }


    @Operation(
        summary = "청원 제목으로 뉴스 검색 및 저장",
        description = "청원의 제목으로 뉴스를 검색하고, 첫 번째 뉴스를 청원의 관련 뉴스로 저장합니다. 검색된 3개의 뉴스는 DB에 저장됩니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/news/petition/{petitionId}")
    fun searchNewsWithPetitionTitle(
        @PathVariable petitionId: Long
    ): ResponseEntity<List<NewsResponseDto>> {
        val result = newsSearchService.searchAndSaveNewsForPetition(petitionId)
        return ResponseEntity.ok(result.map { NewsResponseDto.from(it) })
    }


    @Operation(
        summary = "청원의 관련 뉴스 변경",
        description = "청원의 관련 뉴스를 이미 저장된 다른 뉴스로 변경합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/news/petition/{petitionId}/{newsId}")
    fun updatePetitionNews(
        @PathVariable petitionId: Long,
        @PathVariable newsId: Long
    ): ResponseEntity<PetitionDetailResponseDto> {
        val updatedPetition = newsSearchService.updatePetitionWithExistingNews(petitionId, newsId)
        return ResponseEntity.ok(PetitionDetailResponseDto(updatedPetition))
    }


    @Operation(summary = "저장된 뉴스 목록 조회", description = "DB에 저장된 모든 뉴스 목록을 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/news/saved")
    fun getSavedNews(): ResponseEntity<List<NewsResponseDto>> {
        return ResponseEntity.ok(newsSearchService.getAllNews())
    }
}