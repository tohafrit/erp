package ru.korundm.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * Сущность с описанием таблицы technological_entity_type
 * Date: 24.10.2021
 */
@Entity
@Table(name = "technological_entity_type")
data class TechnologicalEntityType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null, // идентификатор
) {

    @Column(name = "name", nullable = false, unique = true)
    var name = "" // наименование

    @Column(name = "short_name", nullable = false)
    var shortName = "" // короткое наименование

    @Column(name = "multi", nullable = false)
    var multi = false // множественная применяемость

    @Column(name = "comment")
    var comment: String? = null // комментарий
}
