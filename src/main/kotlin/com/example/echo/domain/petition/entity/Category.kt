package com.example.echo.domain.petition.entity

enum class Category(
    val description: String
) {

    POLITICS("정치/선거/국회운영"),
    INVESTIGATION("수사/법무/사법제도"),
    FINANCE("재정/세제/금융/예산"),
    CONSUMER("소비자/공정거래"),
    EDUCATION("교육"),
    SCIENCE("과학기술/정보통신"),
    DIPLOMACY("외교/통일/국방/안보"),
    DISASTER("재난/안전/환경"),
    ADMINISTRATION("행정/지방자치"),
    CULTURE("문화/체육/관광/언론"),
    AGRICULTURE("농업/임업/수산업/축산업"),
    INDUSTRY("산업/통상"),
    HEALTHCARE("보건의료"),
    WELFARE("복지/보훈"),
    LAND("국토/해양/교통"),
    HUMAN("인권/성평등/노동"),
    LOW_BIRTHRATE("저출산/고령화/아동/청소년/가족"),
    OTHERS("기타");

    companion object {

        fun from(description: String): Category? = entries.firstOrNull {
            it.description.equals(description.trim(), ignoreCase = true)
        }
    }
}

