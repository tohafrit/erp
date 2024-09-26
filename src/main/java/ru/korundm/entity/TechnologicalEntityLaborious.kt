package ru.korundm.entity

import javax.persistence.*

/**
 * Сущность с описанием таблицы с информацией о трудоемкостях в технологической документации
 */
@Entity
@Table(name = "technological_entity_laborious")
data class TechnologicalEntityLaborious(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null, // идентификатор
) {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technological_entity_operation_id")
    var technologicalEntityOperation: TechnologicalEntityOperation? = null // операция технологической документации

    @Column(name = "laborious_value")
    var laboriousnessValue: String? = null // значение трудоемкости
}
