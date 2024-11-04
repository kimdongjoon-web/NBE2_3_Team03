package com.example.echo.domain.petition.dto.request

data class NewsSearchRequest(
    val query: String,
    val display: Int = 10,
    val start: Int = 1,
    val sort: SortType = SortType.ACCURACY
) {
    init {
        require(display in 1..100) { "display must be between 1 and 100" }
        require(start in 1..1000) { "start must be between 1 and 1000" }
    }

    enum class SortType(val value: String) {
        ACCURACY("sim"),
        RECENT("date");

        companion object {
            fun fromValue(value: String): SortType? {
                return values().find { it.value == value }
            }
        }
    }
}