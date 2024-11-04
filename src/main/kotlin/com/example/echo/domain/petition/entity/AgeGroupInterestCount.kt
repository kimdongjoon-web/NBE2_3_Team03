package com.example.echo.domain.petition.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "interest")
class AgeGroupInterestCount (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_id")
    var id: Long? = null,

    @Column(name = "age_group")
    val ageGroup: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petition_id", nullable = false)
    @JsonIgnore
    val petition: Petition? = null,

    @Column(name = "interest_count")
    var interestCount: Int = 0
)