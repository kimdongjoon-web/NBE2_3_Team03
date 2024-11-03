package com.example.echo.domain.petition.entity

import jakarta.persistence.*

@Entity
@Table(name = "interest")
class AgeGroupInterestCount (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    val ageGroup: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petition_id", nullable = false)
    val petition: Petition? = null,

    var interestCount: Int = 0
)