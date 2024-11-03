package com.example.echo.domain.petition.service

import com.example.echo.domain.petition.dto.request.NewsSearchRequest
import com.example.echo.domain.petition.dto.response.NewsResponseDto
import com.example.echo.domain.petition.entity.News
import com.example.echo.domain.petition.entity.Petition
import com.example.echo.domain.petition.repository.NewsRepository
import com.example.echo.domain.petition.repository.PetitionRepository
import com.example.echo.domain.petition.util.NaverNewsSearchUtil
import com.example.echo.global.config.NaverProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NewsSearchService(
    private val naverProperties: NaverProperties,
    private val naverNewsSearchUtil: NaverNewsSearchUtil,
    private val newsRepository: NewsRepository,
    private val petitionRepository: PetitionRepository,
) {
    @Transactional
    fun searchNews(request: NewsSearchRequest): List<News> {
        val response = naverNewsSearchUtil.searchNews(
            query = request.query,
            clientId = naverProperties.client.id,
            clientSecret = naverProperties.client.secret,
            display = request.display,
            start = request.start,
            sort = request.sort.value
        )

        return newsRepository.saveAll(response.items.map { it.toDomain() })
    }

    // 새로운 메서드 (검색만)
    fun searchNewsOnly(request: NewsSearchRequest): List<News> {
        val response = naverNewsSearchUtil.searchNews(
            query = request.query,
            clientId = naverProperties.client.id,
            clientSecret = naverProperties.client.secret,
            display = request.display,
            start = request.start,
            sort = request.sort.value
        )

        return response.items.map { it.toDomain() }
    }

    @Transactional
    fun searchAndSaveNewsForPetition(petitionId: Long): List<News> {
        // 1. 청원 조회
        val petition = petitionRepository.findById(petitionId)
            .orElseThrow { NoSuchElementException("Petition not found with id: $petitionId") }

        // 2. 청원의 title을 검색어로 사용
        val query = petition.title ?: throw IllegalStateException("Petition title is null")

        // 3. 뉴스 검색
        val response = naverNewsSearchUtil.searchNews(
            query = query,
            clientId = naverProperties.client.id,
            clientSecret = naverProperties.client.secret,
            display = 3
        )

        if (response.items.isNotEmpty()) {
            // 각 뉴스에 petition 설정하여 저장
            val savedNewsList = response.items.map {
                it.toDomain(petition)
            }.let { newsRepository.saveAll(it) }

            // 첫 번째 뉴스 링크를 청원의 relatedNews에 저장
            petition.relatedNews = savedNewsList.first().link
            petitionRepository.save(petition)

            return savedNewsList
        } else {
            throw NoSuchElementException("No news found for petition: $petitionId")
        }
    }

    // 기존 청원에 저장된 뉴스 연결하는 메서드 추가
    @Transactional
    fun updatePetitionWithExistingNews(petitionId: Long, newsId: Long): Petition {
        val petition = petitionRepository.findById(petitionId)
            .orElseThrow { NoSuchElementException("Petition not found with id: $petitionId") }

        val news = newsRepository.findById(newsId)
            .orElseThrow { NoSuchElementException("News not found with id: $newsId") }

        petition.relatedNews = news.link
        return petitionRepository.save(petition)
    }

    // 저장된 뉴스 목록 조회
    @Transactional(readOnly = true)
    fun getAllNews(): List<NewsResponseDto> {
        return newsRepository.findAll()
            .map { NewsResponseDto.from(it) }
    }
}