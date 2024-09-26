package ru.korundm.entity

import javax.persistence.*

/**
 * Сущность с описанием таблицы faqs
 * @author berezin_mm
 * Date:   18.02.2021
 */
@Entity
@Table(name = "faqs")
data class Faq(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    val id: Long? = null
) {
    @Column(name = "sort", nullable = false)
    var sort = 0 // сортировка

    @Column(name = "question", nullable = false)
    var question = "" // вопрос

    @Column(name = "answer", nullable = false)
    var answer = "" // ответ
}

@Suppress("unused")
object FaqM {
    const val ID = "id"
    const val SORT = "sort"
    const val QUESTION = "question"
    const val ANSWER = "answer"
}