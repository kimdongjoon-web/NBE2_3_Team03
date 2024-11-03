package com.example.echo.domain.petition.dto.response

import com.example.echo.domain.petition.entity.News
import com.example.echo.domain.petition.entity.Petition
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class NewsSearchResponse(
    @JsonProperty("lastBuildDate")
    val lastBuildDate: String,
    @JsonProperty("total")
    val total: Int,
    @JsonProperty("start")
    val start: Int,
    @JsonProperty("display")
    val display: Int,
    @JsonProperty("items")
    val items: List<NewsItemDto>
) {
    fun toDomain(): List<News> {
        return items.map { it.toDomain() }
    }
}

data class NewsItemDto(
    @JsonProperty("title")
    val title: String,
    @JsonProperty("originallink")
    val originalLink: String,
    @JsonProperty("link")
    val link: String,
    @JsonProperty("description")
    val description: String,
    @JsonProperty("pubDate")
    val pubDate: String
) {
    fun toDomain(): News {
        return News(
            title = title.replace("<b>", "").replace("</b>", ""),
            originalLink = originalLink,
            link = link,
            description = description.replace("<b>", "").replace("</b>", ""),
            publishedAt = ZonedDateTime.parse(pubDate, DateTimeFormatter.RFC_1123_DATE_TIME),
            petition = null
        )
    }

    fun toDomain(petition: Petition?): News {
        return News(
            title = title.replace("<b>", "").replace("</b>", ""),
            originalLink = originalLink,
            link = link,
            description = description.replace("<b>", "").replace("</b>", ""),
            publishedAt = ZonedDateTime.parse(pubDate, DateTimeFormatter.RFC_1123_DATE_TIME),
            petition = petition
        )
    }
}