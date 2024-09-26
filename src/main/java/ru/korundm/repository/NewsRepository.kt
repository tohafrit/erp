package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.News

interface NewsRepository : JpaRepository<News, Long> {

    fun findAllByIdIsNotAndTopStatusTrue(id: Long): List<News>
}