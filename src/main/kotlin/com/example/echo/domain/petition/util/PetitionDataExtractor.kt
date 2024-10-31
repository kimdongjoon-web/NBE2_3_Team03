package com.example.echo.domain.petition.util

import com.example.echo.domain.petition.entity.Category
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PetitionDataExtractor {

    companion object {

        fun extractStartDate(period: String): LocalDateTime {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return period.split("~").firstOrNull()
                ?.replace("동의기간", "")?.trim()
                ?.let { startDateString ->
                    LocalDate.parse(startDateString, formatter).atStartOfDay()
                } ?: throw IllegalArgumentException("유효한 시작일이 아닙니다. $period")
        }

        fun extractEndDate(period: String): LocalDateTime {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return period.split("~").getOrNull(1)
                ?.trim()
                ?.let { endDateString ->
                    LocalDate.parse(endDateString, formatter).atStartOfDay()
                } ?: throw IllegalArgumentException("유효한 종료일이 아닙니다. $period")
        }

        fun extractNumber(text: String) : String {
            val regex = "\\d{1,3}(,\\d{3})*".toRegex()
            return regex.find(text)?.value?.replace(",", "")
                ?: throw IllegalArgumentException("유요한 동의자 수가 아닙니다. $text")
        }

        fun
                convertCategory(category: String): Category {
            return Category.from(category) ?: throw IllegalArgumentException("유효한 카테고리가 아닙니다. $category")
        }
    }
}