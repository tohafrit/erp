package ru.korundm.integration.pacs.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Сущность с описанием таблицы AcessPoint
 * @author pakhunov_an
 * Date:   20.03.2021
 */
@Entity
@Table(name = "AcessPoint")
data class PACSAccessPoint(
    @Id
    @Column(name = "id")
    var id: Long? = null // id
) {
    @Column(name = "Name")
    var name = "" // название

    @Column(name = "GIndex")
    var doorIndex: Int? = null
}