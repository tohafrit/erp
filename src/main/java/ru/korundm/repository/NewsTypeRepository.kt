package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.NewsType

interface NewsTypeRepository : JpaRepository<NewsType, Long>