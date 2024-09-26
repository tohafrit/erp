package ru.korundm.entity

import ru.korundm.helper.FileStorable
import java.time.LocalDate
import javax.persistence.*

/**
 * Сущность с описанием таблицы gratitudes
 * @author berezin_mm
 * Date:   16.02.2021
 */
@Entity
@Table(name = "gratitudes")
data class Gratitude(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    val id: Long? = null
) : FileStorable<Gratitude> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    var company: Company? = null // компания

    @Column(name = "date", nullable = false)
    var date: LocalDate? = null // год

    override fun storableId() = this.id

    override fun storableClass() = Gratitude::class
}

@Suppress("unused")
object GratitudeM {
    const val ID = "id"
    const val COMPANY = "company"
    const val DATE = "date"
}