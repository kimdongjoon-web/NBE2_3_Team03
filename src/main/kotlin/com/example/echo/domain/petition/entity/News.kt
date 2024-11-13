package com.example.echo.domain.petition.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "news")
class News(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    val newsId: Long? = null,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "original_link", nullable = false)
    val originalLink: String,

    @Column(name = "link", nullable = false)
    val link: String,

    @Column(name = "description", nullable = false)
    val description: String,

    @Column(name = "published_at", nullable = false)
    val publishedAt: ZonedDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petition_id")
    val petition: Petition? = null
)
