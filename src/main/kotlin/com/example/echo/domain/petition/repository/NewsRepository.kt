package com.example.echo.domain.petition.repository

import com.example.echo.domain.petition.entity.News
import org.springframework.data.jpa.repository.JpaRepository

interface NewsRepository : JpaRepository<News, Long>