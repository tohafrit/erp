package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Faq

interface FaqRepository : JpaRepository<Faq, Long> {

    fun findAllByIdIsNotNullOrderBySortDescIdAsc(): List<Faq>
}