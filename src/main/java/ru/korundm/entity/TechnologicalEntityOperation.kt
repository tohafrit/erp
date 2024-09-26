package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant
import ru.korundm.helper.RowCountable
import javax.persistence.*

/**
 * Сущность с описанием таблицы с информацией об операциях в технологической документации
 */
@Entity
@Table(name = "technological_entity_operation")
data class TechnologicalEntityOperation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null // идентификатор
) : RowCountable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technological_entity_id")
    var technologicalEntity: TechnologicalEntity? = null // технологическая документация

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technological_entity_star_id")
    var technologicalEntityStar: TechnologicalEntityStar? = null // примечание

    @Column(name = "sort", nullable = false)
    var sort = 1 // сортировка

    @OneToMany(mappedBy = "technologicalEntityOperation")
    var technologicalEntityLaboriousList = mutableListOf<TechnologicalEntityLaborious>()// список трудоемкостей

    @Formula(BaseConstant.PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}
