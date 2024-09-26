package ru.korundm.entity

import javax.persistence.*

/**
 * Сущность с описанием таблицы классификационных групп (ex. ECOPLAN.SPECIAL_SPECIAL)
 * @author pakhunov_an
 * Date:   27.02.2019
 */
@Entity
@Table(name = "classification_groups")
data class ClassificationGroup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var version = 0L

    @Column(name = "number", precision = 2, unique = true)
    var number = 0 // номер

    @Column(name = "characteristic", length = 128)
    var characteristic = "" // характеристика

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий
}