package com.example.echo.domain.petition.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/petitions")
@Tag(name = "Petition Crawling Controller", description = "청원 크롤링 API")
class PetitionCrawlController(

) {
}