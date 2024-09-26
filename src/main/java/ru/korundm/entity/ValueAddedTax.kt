package ru.korundm.entity

import java.time.LocalDate
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием ставки НДС
 * @author pakhunov_an
 * Date:   18.10.2019
 */
@Entity
@Table(name = "value_added_tax")
data class ValueAddedTax(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Column(name = "name", length = 128)
    var name = "" // наименование

    @Column(name = "date_from")
    var dateFrom: LocalDate = LocalDate.MIN // дата начала действия

    @Column(name = "date_to")
    var dateTo: LocalDate? = null // дата окончания действия

    @Column(name = "value")
    var value = .0 // величина ставки в %

    @OneToMany(mappedBy = "vat")
    var lotList = mutableListOf<Lot>() // партии
}